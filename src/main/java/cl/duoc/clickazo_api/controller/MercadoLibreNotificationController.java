package cl.duoc.clickazo_api.controller;

import cl.duoc.clickazo_api.mercadolibre.MercadoLibreNotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/external/mercadolibre")
@CrossOrigin(origins = "*")
public class MercadoLibreNotificationController {

    private final MercadoLibreNotificationService notificationService;

    public MercadoLibreNotificationController(MercadoLibreNotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/notificaciones")
    public ResponseEntity<Void> recibirNotificacion(@RequestBody String body, @RequestHeader Map<String, String> headers) {
        log.info("Notificacion recibida desde Mercado Libre. Headers: {}, Body: {}", headers, body);
        notificationService.procesarNotificacion(body, headers);
        return ResponseEntity.ok().build();
    }
}
