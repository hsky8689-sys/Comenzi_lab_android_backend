package facultate.android_backend.Repository;

import facultate.android_backend.Domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductsRepository extends JpaRepository<Product,Long> {
    @Modifying
    @Query( value = "DELETE FROM Product p WHERE p.id = :id")
    int deleteProduct(Long id);
}
