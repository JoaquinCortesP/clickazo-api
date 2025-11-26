package cl.duoc.clickazo_api.service;
import cl.duoc.clickazo_api.model.Product;
import cl.duoc.clickazo_api.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository repo;

    public ProductService(ProductRepository repo) {
        this.repo = repo;
    }

    public List<Product> findAll() {
        return repo.findAll();
    }

    public List<Product> search(String q, Integer minDiscount) {
        if (q != null && !q.isBlank()) {
            return repo.findByTitleContainingIgnoreCase(q);
        }
        if (minDiscount != null) {
            return repo.findByDiscountPercentGreaterThanEqual(minDiscount);
        }
        return repo.findAll();
    }

    public Product findById(Long id) {
        return repo.findById(id).orElseThrow();
    }

    public Product create(Product product) {
        if (product.getOriginalPrice() != null && product.getDiscountedPrice() != null) {
            double op = product.getOriginalPrice();
            double dp = product.getDiscountedPrice();
            int pct = (int) Math.round(100 - (dp * 100 / op));
            product.setDiscountPercent(pct);
        }
        if (product.getActive() == null) {
            product.setActive(true);
        }
        return repo.save(product);
    }

    public Product update(Long id, Product updated) {
        Product existing = repo.findById(id).orElseThrow();
        existing.setStoreName(updated.getStoreName());
        existing.setTitle(updated.getTitle());
        existing.setDescription(updated.getDescription());
        existing.setImageUrl(updated.getImageUrl());
        existing.setAffiliateUrl(updated.getAffiliateUrl());
        existing.setOriginalPrice(updated.getOriginalPrice());
        existing.setDiscountedPrice(updated.getDiscountedPrice());
        existing.setDiscountPercent(updated.getDiscountPercent());
        existing.setCategory(updated.getCategory());
        existing.setActive(updated.getActive());
        return repo.save(existing);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}

