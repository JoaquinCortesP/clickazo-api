package cl.duoc.clickazo_api.dto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExternalProductDto {

    private String nombre;
    private Double precio;
    private String tienda;
    private String imagenUrl;
    private String affiliateUrl;
}
