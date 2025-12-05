package cl.duoc.clickazo_api.mercadolibre;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class MercadoLibreAuthService {

    private final WebClient webClient;
    private final String clientId;
    private final String clientSecret;
    private final String refreshToken;
    private final String grantType;

    private final AtomicReference<String> cachedToken = new AtomicReference<>();
    private final AtomicReference<Instant> expiresAt = new AtomicReference<>(Instant.EPOCH);

    public MercadoLibreAuthService(
            WebClient.Builder builder,
            @Value("${meli.client-id}") String clientId,
            @Value("${meli.client-secret}") String clientSecret,
            @Value("${meli.refresh-token}") String refreshToken,
            @Value("${meli.grant-type:refresh_token}") String grantType
    ) {
        this.webClient = builder.baseUrl("http://developers.mercadolibre.com").build();
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.refreshToken = refreshToken;
        this.grantType = grantType;
    }

    // Obtains a valid Mercado Libre access token, refreshing it when expired.
    public synchronized String getAccessToken() {
        if (isTokenValid()) {
            return cachedToken.get();
        }
        refreshToken();
        return cachedToken.get();
    }

    // Checks if cached token exists and is not expired.
    private boolean isTokenValid() {
        String token = cachedToken.get();
        Instant expiry = expiresAt.get();
        return token != null && Instant.now().isBefore(expiry.minusSeconds(30));
    }

    // Calls Mercado Libre OAuth endpoint to refresh the access token.
    private void refreshToken() {
        try {
            TokenResponse response = webClient.post()
                    .uri("/oauth/token")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(BodyInserters.fromFormData("grant_type", grantType)
                            .with("client_id", clientId)
                            .with("client_secret", clientSecret)
                            .with("refresh_token", refreshToken))
                    .retrieve()
                    .bodyToMono(TokenResponse.class)
                    .block();

            if (response == null || response.getAccessToken() == null || response.getExpiresIn() == null) {
                throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "No se pudo obtener token de Mercado Libre");
            }

            cachedToken.set(response.getAccessToken());
            expiresAt.set(Instant.now().plusSeconds(response.getExpiresIn()));
        } catch (WebClientResponseException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Error al obtener token de Mercado Libre");
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "No se pudo obtener token de Mercado Libre");
        }
    }

    private static class TokenResponse {
        private String access_token;
        private Long expires_in;

        public String getAccessToken() {
            return access_token;
        }

        public Long getExpiresIn() {
            return expires_in;
        }
    }
}
