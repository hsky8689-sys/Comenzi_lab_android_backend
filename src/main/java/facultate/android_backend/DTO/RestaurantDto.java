package facultate.android_backend.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class RestaurantDto {
    private Long id,ownerId;
    private String name,adress;
    private List<ProductDto> products;
}
