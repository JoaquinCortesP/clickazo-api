package cl.duoc.clickazo_api.repository;
import cl.duoc.clickazo_api.model.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    List<Favorite> findByUsername(String username);
}
