package facultate.android_backend.Controller.Observer;

import facultate.android_backend.DTO.ProductDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductOperation {
    private ProductDto innerDto;
    private CrudOperation operation;
}
