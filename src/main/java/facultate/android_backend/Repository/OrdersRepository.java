package facultate.android_backend.Repository;

import facultate.android_backend.Domain.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrdersRepository extends JpaRepository<Order,Long> {
    @Query(value = "SELECT o from Order o WHERE o.user.id = :userId")
    public List<Order> getUserOrders(long userId);
    @Query("""
               SELECT SUM(po.quantityOrdered * po.pricePerProduct) FROM ProductOrder po
               WHERE po.order.id = :orderId
           """)
    public long computeOrderTotalCost(long orderId);
    @Query(value = """
                    SELECT o FROM Order o where o.user.username = :username
                    """)
    Page<Order> findByUser_Username(String username, Pageable pageable);
    Order getOrderByIdAndUser_UsernameAfter(Long id, String userUsernameAfter);
}
