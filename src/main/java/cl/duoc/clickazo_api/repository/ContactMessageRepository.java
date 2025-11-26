package cl.duoc.clickazo_api.repository;
import cl.duoc.clickazo_api.model.ContactMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactMessageRepository extends JpaRepository<ContactMessage, Long> {
}
