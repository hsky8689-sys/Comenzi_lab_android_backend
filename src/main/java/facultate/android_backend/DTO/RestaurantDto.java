package facultate.android_backend.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class RestaurantDto {
    private Long id,ownerId;
    private String name,adress,image;
    private LocationDto location;
    // doar la cautarea dupa locatie: cat de departe e de punctul ales, in km
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Double distanceKm;
    private List<ProductDto> products;
}
