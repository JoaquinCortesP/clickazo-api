package cl.duoc.clickazo_api.model;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String storeName;
    private String title;
    private String description;
    private String imageUrl;
    private String affiliateUrl;

    private Double originalPrice;
    private Double discountedPrice;
    private Integer discountPercent;

    private String category;
    private Boolean active;
}

