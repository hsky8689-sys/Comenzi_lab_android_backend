package facultate.android_backend.Repository;

import facultate.android_backend.Domain.ProductOrder;
import facultate.android_backend.Domain.ProductOrderKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductOrdersRepository extends JpaRepository<ProductOrder, ProductOrderKey> {
}
