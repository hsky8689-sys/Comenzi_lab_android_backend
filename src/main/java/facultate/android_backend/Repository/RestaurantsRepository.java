package facultate.android_backend.Repository;

import facultate.android_backend.Domain.Product;
import facultate.android_backend.Domain.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RestaurantsRepository extends JpaRepository<Restaurant,Long> {
    @Query("SELECT p from Product p where p.restaurant.id = :restaurantId")
    public List<Product> getAllProducts(long restaurantId);
    @Modifying
    @Query( value = "DELETE FROM Restaurant r WHERE r.id = :id")
    int deleteRestaurant(Long id);
}
