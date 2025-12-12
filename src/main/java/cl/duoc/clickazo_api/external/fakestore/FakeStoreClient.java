package cl.duoc.clickazo_api.external.fakestore;

import cl.duoc.clickazo_api.dto.ExternalProductDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FakeStoreClient {

    private static final String SITE_ID = "FAKESTORE";
    private static final String PERMALINK_BASE = "https://fakestoreapi.com/products/";

    private final WebClient webClient;

    public FakeStoreClient(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("https://fakestoreapi.com").build();
    }

    public List<ExternalProductDto> getHomeProducts() {
        List<FakeStoreProductDto> products = fetchProducts(20);
        return mapProducts(products);
    }

    public List<ExternalProductDto> search(String query) {
        String term = query != null ? query.trim().toLowerCase(Locale.ROOT) : "";
        List<FakeStoreProductDto> products = fetchProducts(null);

        if (term.isBlank()) {
            return mapProducts(products);
        }

        return products.stream()
                .filter(p -> matchesTerm(p, term))
                .map(this::mapToDto)
                .toList();
    }

    public List<ExternalProductDto> getByIds(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }

        return ids.stream()
                .map(this::fetchById)
                .filter(Objects::nonNull)
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private boolean matchesTerm(FakeStoreProductDto product, String term) {
        return (product.getTitle() != null && product.getTitle().toLowerCase(Locale.ROOT).contains(term))
                || (product.getCategory() != null && product.getCategory().toLowerCase(Locale.ROOT).contains(term));
    }

    private List<FakeStoreProductDto> fetchProducts(Integer limit) {
        try {
            log.info("Consultando FakeStore products con limit {}", limit);
            List<FakeStoreProductDto> products = webClient.get()
                    .uri(uriBuilder -> {
                        var builder = uriBuilder.path("/products");
                        if (limit != null) {
                            builder.queryParam("limit", limit);
                        }
                        return builder.build();
                    })
                    .retrieve()
                    .bodyToFlux(FakeStoreProductDto.class)
                    .collectList()
                    .block();

            return products != null ? products : List.of();
        } catch (WebClientResponseException ex) {
            log.error("FakeStore respondio con error {}: {}", ex.getStatusCode(), ex.getResponseBodyAsString());
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "FakeStore devolvio un error al obtener productos");
        } catch (Exception ex) {
            log.error("Error inesperado al consultar FakeStore: {}", ex.getMessage());
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "No se pudo obtener productos de FakeStore");
        }
    }

    private FakeStoreProductDto fetchById(String id) {
        if (id == null || id.isBlank()) {
            return null;
        }
        try {
            log.info("Consultando FakeStore product id={}", id);
            return webClient.get()
                    .uri("/products/{id}", id)
                    .retrieve()
                    .bodyToMono(FakeStoreProductDto.class)
                    .block();
        } catch (WebClientResponseException ex) {
            log.warn("Producto {} no disponible en FakeStore. Status: {}", id, ex.getStatusCode());
            return null;
        } catch (Exception ex) {
            log.error("Error inesperado al consultar producto {} en FakeStore: {}", id, ex.getMessage());
            return null;
        }
    }

    private List<ExternalProductDto> mapProducts(List<FakeStoreProductDto> products) {
        return products.stream()
                .map(this::mapToDto)
                .toList();
    }

    private ExternalProductDto mapToDto(FakeStoreProductDto product) {
        ExternalProductDto dto = new ExternalProductDto();
        dto.setId(product.getId() != null ? product.getId().toString() : null);
        dto.setTitle(product.getTitle());
        dto.setPrice(product.getPrice());
        dto.setOriginalPrice(product.getPrice());
        dto.setDiscountPercentage(0.0);
        dto.setPermalink(product.getId() != null ? PERMALINK_BASE + product.getId() : PERMALINK_BASE);
        dto.setThumbnail(product.getImage());
        dto.setSiteId(SITE_ID);
        dto.setSellerNickname("FakeStore");
        dto.setSellerId(null);
        dto.setLastUpdated(null);
        dto.setDateCreated(null);
        dto.setStopTime(null);
        return dto;
    }
}
