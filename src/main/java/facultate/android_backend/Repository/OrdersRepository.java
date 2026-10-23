package facultate.android_backend.Repository;

import facultate.android_backend.Domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdersRepository extends JpaRepository<Order,Long> {
    @Query(value = "SELECT o from Order o WHERE o.user.id = :userId")
    public List<Order> getUserOrders(long userId);
    @Query("""
               SELECT SUM(po.quantityOrdered * po.pricePerProduct) FROM ProductOrder po
               WHERE po.order.id = :orderId
           """)
    public long computeOrderTotalCost(long orderId);
}
