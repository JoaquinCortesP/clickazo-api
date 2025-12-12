package cl.duoc.clickazo_api.external.fakestore;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FakeStoreProductDto {
    private Long id;
    private String title;
    private Double price;
    private String description;
    private String category;
    private String image;
    private Rating rating;

    @Getter
    @Setter
    public static class Rating {
        private Double rate;
        private Integer count;
    }
}
