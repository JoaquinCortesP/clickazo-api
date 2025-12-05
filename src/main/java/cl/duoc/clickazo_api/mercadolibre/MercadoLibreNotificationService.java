package cl.duoc.clickazo_api.mercadolibre;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class MercadoLibreNotificationService {

    public void procesarNotificacion(String body, Map<String, String> headers) {
        log.info("Procesando notificacion Mercado Libre. Headers: {}, Body: {}", headers, body);
    }
}
