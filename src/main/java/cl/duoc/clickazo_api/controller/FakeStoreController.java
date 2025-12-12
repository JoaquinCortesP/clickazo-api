package cl.duoc.clickazo_api.controller;

import cl.duoc.clickazo_api.dto.ExternalProductDto;
import cl.duoc.clickazo_api.external.fakestore.FakeStoreClient;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/external/fakestore")
@CrossOrigin(origins = "*")
public class FakeStoreController {

    private final FakeStoreClient client;

    public FakeStoreController(FakeStoreClient client) {
        this.client = client;
    }

    @GetMapping("/home")
    @Operation(summary = "Obtiene productos externos para la portada", description = "Consulta FakeStore API y retorna productos ordenados para la pagina inicial.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resultados obtenidos correctamente"),
            @ApiResponse(responseCode = "502", description = "Error al comunicarse con FakeStore"),
            @ApiResponse(responseCode = "503", description = "FakeStore no disponible")
    })
    public List<ExternalProductDto> home() {
        return client.getHomeProducts();
    }

    @GetMapping("/search")
    @Operation(summary = "Busca productos en FakeStore", description = "Filtra productos de FakeStore por titulo o categoria, ignorando mayusculas/minusculas.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resultados obtenidos correctamente"),
            @ApiResponse(responseCode = "502", description = "Error al comunicarse con FakeStore"),
            @ApiResponse(responseCode = "503", description = "FakeStore no disponible")
    })
    public List<ExternalProductDto> search(@Parameter(description = "Texto a buscar en FakeStore") @RequestParam String q) {
        return client.search(q);
    }

    @GetMapping("/favorites")
    @Operation(summary = "Obtiene detalles de favoritos externos", description = "Recupera productos desde FakeStore usando la lista de ids proporcionada.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Favoritos obtenidos correctamente"),
            @ApiResponse(responseCode = "502", description = "Error al comunicarse con FakeStore"),
            @ApiResponse(responseCode = "503", description = "FakeStore no disponible")
    })
    public List<ExternalProductDto> favorites(@Parameter(description = "IDs de productos separados por coma") @RequestParam String ids) {
        List<String> parsedIds = Arrays.stream(ids.split(","))
                .map(String::trim)
                .filter(id -> !id.isEmpty())
                .toList();
        return client.getByIds(parsedIds);
    }
}
