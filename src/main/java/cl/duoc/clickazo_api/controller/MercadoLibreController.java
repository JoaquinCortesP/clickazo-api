package cl.duoc.clickazo_api.controller;

import cl.duoc.clickazo_api.dto.ExternalProductDto;
import cl.duoc.clickazo_api.mercadolibre.MercadoLibreClient;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/external/mercadolibre")
@CrossOrigin(origins = "*")
public class MercadoLibreController {

    private final MercadoLibreClient client;

    public MercadoLibreController(MercadoLibreClient client) {
        this.client = client;
    }

    @GetMapping("/search")
    @Operation(summary = "Busca productos en Mercado Libre", description = "Consulta la API oficial de Mercado Libre Chile ordenada por precio ascendente.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resultados obtenidos correctamente"),
            @ApiResponse(responseCode = "502", description = "Error al comunicarse con Mercado Libre"),
            @ApiResponse(responseCode = "503", description = "Mercado Libre no disponible")
    })
    // Returns search results ordered by price using Mercado Libre data.
    public List<ExternalProductDto> search(@Parameter(description = "Texto a buscar en Mercado Libre") @RequestParam String q) {
        return client.search(q);
    }

    @GetMapping("/favorites")
    @Operation(summary = "Obtiene detalles de favoritos", description = "Devuelve detalles y ordena favoritos por recencia o descuento.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Favoritos obtenidos correctamente"),
            @ApiResponse(responseCode = "502", description = "Error al comunicarse con Mercado Libre"),
            @ApiResponse(responseCode = "503", description = "Mercado Libre no disponible")
    })
    // Returns favorite items ordered by recency, discount, or price.
    public List<ExternalProductDto> favorites(@Parameter(description = "IDs de items separados por coma") @RequestParam String ids) {
        return client.favorites(ids);
    }
}
