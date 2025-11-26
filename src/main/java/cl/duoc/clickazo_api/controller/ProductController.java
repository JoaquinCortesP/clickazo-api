package cl.duoc.clickazo_api.controller;

import cl.duoc.clickazo_api.model.Product;
import cl.duoc.clickazo_api.service.ProductService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @GetMapping
    public List<Product> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Product getById(@PathVariable Long id) {
        return service.findById(id);
    }

    @GetMapping("/search")
    public List<Product> search(
            @RequestParam(required = false, name = "q") String q,
            @RequestParam(required = false, name = "minDiscount") Integer minDiscount
    ) {
        return service.search(q, minDiscount);
    }

    @PostMapping
    public Product create(@RequestBody Product product) {
        return service.create(product);
    }

    @PutMapping("/{id}")
    public Product update(@PathVariable Long id, @RequestBody Product product) {
        return service.update(id, product);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
