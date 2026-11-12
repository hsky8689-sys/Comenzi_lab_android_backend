package facultate.android_backend.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProductDto {
    private Long id;
    private long price;
    private String name;
    private String image;
    // doar pentru produsele dintr-o comanda: cate bucati s-au comandat (price e atunci pretul de la momentul comenzii)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer quantity;
    private long restaurant;
}
