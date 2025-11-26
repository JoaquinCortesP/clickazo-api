package cl.duoc.clickazo_api.mercadolibre;
import cl.duoc.clickazo_api.dto.ExternalProductDto;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class MercadoLibreClient {

    private final WebClient webClient;

    public MercadoLibreClient(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("https://api.mercadolibre.com").build();
    }

    public List<ExternalProductDto> search(String query) {
        MercadoLibreSearchResponse response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/sites/MLC/search")
                        .queryParam("q", query)
                        .queryParam("limit", 12)
                        .build())
                .retrieve()
                .bodyToMono(MercadoLibreSearchResponse.class)
                .block();

        if (response == null || response.getResults() == null) {
            return List.of();
        }

        return response.getResults().stream().map(item -> {
            ExternalProductDto dto = new ExternalProductDto();
            dto.setNombre(item.getTitle());
            dto.setPrecio(item.getPrice());
            dto.setTienda("Mercado Libre");
            dto.setImagenUrl(item.getThumbnail());
            dto.setAffiliateUrl(item.getPermalink());
            return dto;
        }).toList();
    }
}
