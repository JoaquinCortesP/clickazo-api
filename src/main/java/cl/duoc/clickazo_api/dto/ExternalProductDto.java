package cl.duoc.clickazo_api.dto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExternalProductDto {

    private String id;
    private String title;
    private Double price;
    private Double originalPrice;
    private Double discountPercentage;
    private String permalink;
    private String thumbnail;
    private String siteId;
    private Long sellerId;
    private String sellerNickname;
    private String lastUpdated;
    private String dateCreated;
    private String stopTime;
}
