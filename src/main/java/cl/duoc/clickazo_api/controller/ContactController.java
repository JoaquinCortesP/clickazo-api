package cl.duoc.clickazo_api.controller;
import cl.duoc.clickazo_api.model.ContactMessage;
import cl.duoc.clickazo_api.repository.ContactMessageRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contact")
@CrossOrigin(origins = "*")
public class ContactController {

    private final ContactMessageRepository repo;

    public ContactController(ContactMessageRepository repo) {
        this.repo = repo;
    }

    @PostMapping
    public ContactMessage sendMessage(@RequestBody ContactMessage msg) {
        return repo.save(msg);
    }
}

