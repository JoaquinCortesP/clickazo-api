package cl.duoc.clickazo_api.mercadolibre;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MercadoLibreItem {

    private String id;
    private String title;
    private Double price;
    private String thumbnail;
    private String permalink;
}
