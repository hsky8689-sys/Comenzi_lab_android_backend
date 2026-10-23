package facultate.android_backend.Service;

import facultate.android_backend.Domain.Order;
import facultate.android_backend.Domain.User;
import facultate.android_backend.Repository.OrdersRepository;
import facultate.android_backend.Repository.ProductsRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrdersService {
    private final OrdersRepository ordersRepository;
    private final ProductsRepository productsRepository;
    private final EntityManager entityManager;
    public OrdersService(OrdersRepository ordersRepository,
                         ProductsRepository productsRepository,
                         EntityManager entityManager) {
        this.ordersRepository = ordersRepository;
        this.productsRepository = productsRepository;
        this.entityManager = entityManager;
    }
    public List<Order> getUserOrders(long userId){
        return ordersRepository.getUserOrders(userId);
    }
    public Optional<Order> placeOrder(Order order){
        try {
            User mock = new User();
            mock.setId(order.getUser().getId());
            entityManager.persist(mock);
            System.out.println("???");
            return Optional.of(ordersRepository.save(order));
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return Optional.empty();
        }
        }
    @Transactional
    public long computeTotalAmountSpent(long userId){
        return getUserOrders(userId).stream()
                             .map(o->
                                     (Long) o.getProductAppearences().stream()
                                             .map(pa -> pa.getPricePerProduct() * pa.getQuantityOrdered())
                                             .mapToLong(Long::longValue).sum()
                            ).mapToLong(Long::longValue).sum();
    }
    public long computeOrderCosts(long orderId){
        return ordersRepository.computeOrderTotalCost(orderId);
    }
}
