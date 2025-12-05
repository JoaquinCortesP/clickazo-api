package cl.duoc.clickazo_api.mercadolibre;

import cl.duoc.clickazo_api.dto.ExternalProductDto;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;

@Service
public class MercadoLibreClient {

    private static final String SITE_ID = "MLC";

    private final WebClient webClient;
    private final MercadoLibreAuthService authService;

    public MercadoLibreClient(WebClient.Builder builder, MercadoLibreAuthService authService) {
        this.webClient = builder.baseUrl("http://developers.mercadolibre.com").build();
        this.authService = authService;
    }

    // Searches Mercado Libre items ordered by price ascending.
    public List<ExternalProductDto> search(String query) {
        String token = authService.getAccessToken();
        try {
            MercadoLibreSearchResponse response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/sites/{siteId}/search")
                            .queryParam("q", query)
                            .queryParam("sort", "price_asc")
                            .queryParam("limit", 50)
                            .build(SITE_ID))
                    .headers(h -> h.setBearerAuth(token))
                    .retrieve()
                    .bodyToMono(MercadoLibreSearchResponse.class)
                    .block();

            if (response == null || response.getResults() == null) {
                return List.of();
            }

            return response.getResults().stream()
                    .map(this::mapToDto)
                    .sorted(Comparator.comparing(dto -> dto.getPrice() != null ? dto.getPrice() : Double.MAX_VALUE))
                    .toList();
        } catch (WebClientResponseException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Mercado Libre devolvio un error al buscar");
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "No se pudo completar la busqueda en Mercado Libre");
        }
    }

    // Retrieves details for favorite item ids and orders them by recency or discount.
    public List<ExternalProductDto> favorites(String ids) {
        if (ids == null || ids.isBlank()) {
            return List.of();
        }

        String token = authService.getAccessToken();
        try {
            List<MercadoLibreItemsResponseItem> response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/items")
                            .queryParam("ids", ids)
                            .build())
                    .headers(h -> h.setBearerAuth(token))
                    .retrieve()
                    .bodyToFlux(MercadoLibreItemsResponseItem.class)
                    .collectList()
                    .block();

            if (response == null) {
                return List.of();
            }

            List<ExternalProductDto> mapped = response.stream()
                    .filter(item -> item.getCode() == 200 && item.getBody() != null)
                    .map(MercadoLibreItemsResponseItem::getBody)
                    .map(this::mapToDto)
                    .toList();

            return mapped.stream()
                    .sorted(this::compareFavorites)
                    .toList();
        } catch (WebClientResponseException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Mercado Libre devolvio un error al obtener favoritos");
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "No se pudo obtener favoritos de Mercado Libre");
        }
    }

    // Maps Mercado Libre item data to the external DTO used by the API.
    private ExternalProductDto mapToDto(MercadoLibreItem item) {
        ExternalProductDto dto = new ExternalProductDto();
        dto.setId(item.getId());
        dto.setTitle(item.getTitle());
        dto.setPrice(item.getPrice());
        dto.setOriginalPrice(item.getOriginalPrice());
        dto.setDiscountPercentage(calculateDiscount(item.getPrice(), item.getOriginalPrice()));
        dto.setPermalink(item.getPermalink());
        dto.setThumbnail(item.getThumbnail());
        dto.setSiteId(item.getSiteId());

        Long sellerId = item.getSellerId();
        if (sellerId == null && item.getSeller() != null) {
            sellerId = item.getSeller().getId();
        }
        dto.setSellerId(sellerId);
        if (item.getSeller() != null) {
            dto.setSellerNickname(item.getSeller().getNickname());
        }

        dto.setLastUpdated(item.getLastUpdated());
        dto.setDateCreated(item.getDateCreated());
        dto.setStopTime(item.getStopTime());
        return dto;
    }

    // Calculates discount percentage when original price exists.
    private Double calculateDiscount(Double price, Double originalPrice) {
        if (price == null || originalPrice == null || originalPrice == 0) {
            return null;
        }
        double pct = 100 - ((price * 100) / originalPrice);
        return Math.round(pct * 100.0) / 100.0;
    }

    // Compares favorite items prioritizing recency, then discount, then price.
    private int compareFavorites(ExternalProductDto a, ExternalProductDto b) {
        Instant aDate = firstNonNullDate(a);
        Instant bDate = firstNonNullDate(b);

        if (aDate != null && bDate != null) {
            return bDate.compareTo(aDate);
        }
        if (aDate != null) {
            return -1;
        }
        if (bDate != null) {
            return 1;
        }

        Double aDiscount = a.getDiscountPercentage() != null ? a.getDiscountPercentage() : 0.0;
        Double bDiscount = b.getDiscountPercentage() != null ? b.getDiscountPercentage() : 0.0;
        int discountCompare = bDiscount.compareTo(aDiscount);
        if (discountCompare != 0) {
            return discountCompare;
        }

        Double aPrice = a.getPrice() != null ? a.getPrice() : Double.MAX_VALUE;
        Double bPrice = b.getPrice() != null ? b.getPrice() : Double.MAX_VALUE;
        return aPrice.compareTo(bPrice);
    }

    // Returns the most relevant date for ordering favorites.
    private Instant firstNonNullDate(ExternalProductDto dto) {
        Instant last = parseDate(dto.getLastUpdated());
        if (last != null) {
            return last;
        }
        return parseDate(dto.getDateCreated());
    }

    // Parses an ISO date string to Instant.
    private Instant parseDate(String date) {
        if (date == null || date.isBlank()) {
            return null;
        }
        try {
            return OffsetDateTime.parse(date).toInstant();
        } catch (Exception ex) {
            return null;
        }
    }
}
