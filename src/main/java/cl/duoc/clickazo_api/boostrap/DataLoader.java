package cl.duoc.clickazo_api.boostrap;

import cl.duoc.clickazo_api.model.*;
import cl.duoc.clickazo_api.repository.ProductRepository;
import cl.duoc.clickazo_api.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private final ProductRepository productRepo;
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(ProductRepository productRepo,
                      UserRepository userRepo,
                      PasswordEncoder passwordEncoder) {
        this.productRepo = productRepo;
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (productRepo.count() == 0) {
            productRepo.save(Product.builder()
                    .storeName("Falabella")
                    .title("TV 55 4K")
                    .description("Televisor Smart 4K UHD")
                    .imageUrl("https://via.placeholder.com/300x200")
                    .affiliateUrl("https://www.falabella.com/falabella-cl/product/123456?ref=clickazo")
                    .originalPrice(499990.0)
                    .discountedPrice(299990.0)
                    .discountPercent(40)
                    .category("Tecnología")
                    .active(true)
                    .build());

            productRepo.save(Product.builder()
                    .storeName("Paris")
                    .title("Zapatillas Urbanas")
                    .description("Zapatillas urbanas unisex")
                    .imageUrl("https://via.placeholder.com/300x200")
                    .affiliateUrl("https://www.paris.cl/producto/123456?ref=clickazo")
                    .originalPrice(59990.0)
                    .discountedPrice(39990.0)
                    .discountPercent(33)
                    .category("Moda")
                    .active(true)
                    .build());
        }

        if (userRepo.count() == 0) {
            userRepo.save(User.builder()
                    .username("admin")
                    .email("admin@clickazo.cl")
                    .password(passwordEncoder.encode("admin123"))
                    .role(Role.ADMIN)
                    .build());
        }
    }
}
