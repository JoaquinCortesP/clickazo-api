package cl.duoc.clickazo_api.mercadolibre;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MercadoLibreItem {

    private String id;
    private String title;
    private Double price;

    @JsonProperty("original_price")
    private Double originalPrice;

    private String thumbnail;
    private String permalink;

    @JsonProperty("site_id")
    private String siteId;

    @JsonProperty("seller_id")
    private Long sellerId;

    private Seller seller;

    @JsonProperty("last_updated")
    private String lastUpdated;

    @JsonProperty("date_created")
    private String dateCreated;

    @JsonProperty("stop_time")
    private String stopTime;

    @Getter
    @Setter
    public static class Seller {
        private Long id;
        private String nickname;
    }
}
