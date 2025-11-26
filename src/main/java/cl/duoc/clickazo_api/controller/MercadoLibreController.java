package cl.duoc.clickazo_api.controller;
import cl.duoc.clickazo_api.dto.ExternalProductDto;
import cl.duoc.clickazo_api.mercadolibre.MercadoLibreClient;
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
    public List<ExternalProductDto> search(@RequestParam String q) {
        return client.search(q);
    }
}
