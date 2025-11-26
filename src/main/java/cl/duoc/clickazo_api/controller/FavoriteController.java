package cl.duoc.clickazo_api.controller;
import cl.duoc.clickazo_api.model.Favorite;
import cl.duoc.clickazo_api.repository.FavoriteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/favorites")
@CrossOrigin(origins = "*")
public class FavoriteController {

    private final FavoriteRepository repo;

    public FavoriteController(FavoriteRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<Favorite> getMyFavorites(@AuthenticationPrincipal UserDetails user) {
        return repo.findByUsername(user.getUsername());
    }

    @PostMapping
    public Favorite addFavorite(
            @AuthenticationPrincipal UserDetails user,
            @RequestBody Map<String, Long> body
    ) {
        Long productId = body.get("productId");
        if (productId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "productId requerido");
        }
        Favorite fav = Favorite.builder()
                .username(user.getUsername())
                .productId(productId)
                .build();
        return repo.save(fav);
    }

    @DeleteMapping("/{id}")
    public void deleteFavorite(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails user
    ) {
        Favorite fav = repo.findById(id).orElseThrow();
        if (!fav.getUsername().equals(user.getUsername())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No puedes borrar este favorito");
        }
        repo.delete(fav);
    }
}
