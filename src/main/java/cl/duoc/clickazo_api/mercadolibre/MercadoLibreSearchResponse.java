package cl.duoc.clickazo_api.mercadolibre;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MercadoLibreSearchResponse {

    private List<MercadoLibreItem> results;
}
