package facultate.android_backend.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProductDto {
    private Long id;
    private long price;
    private String name;
    private long restaurant;
}
