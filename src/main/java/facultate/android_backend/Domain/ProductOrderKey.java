package facultate.android_backend.Domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductOrderKey implements Serializable {
    @Column(name="order_id")
    long orderId;
    @Column(name="product_id")
    long productId;
}
