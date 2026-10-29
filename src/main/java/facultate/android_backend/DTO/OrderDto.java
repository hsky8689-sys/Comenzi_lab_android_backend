package facultate.android_backend.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class OrderDto {
    private Long id;
    private LocalDateTime orderTime;
    private List<ProductDto> products;
    private Long totalPrice;
    private String idempotencyKey;
}
