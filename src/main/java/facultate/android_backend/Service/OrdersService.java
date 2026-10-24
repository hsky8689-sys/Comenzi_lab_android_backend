package facultate.android_backend.Service;

import facultate.android_backend.Domain.Order;
import facultate.android_backend.Domain.ProductOrder;
import facultate.android_backend.Domain.User;
import facultate.android_backend.Repository.OrdersRepository;
import facultate.android_backend.Repository.ProductOrdersRepository;
import facultate.android_backend.Repository.ProductsRepository;
import facultate.android_backend.Repository.UsersRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrdersService {
    private final OrdersRepository ordersRepository;
    private final ProductsRepository productsRepository;
    private final ProductOrdersRepository productOrdersRepository;
    private final EntityManager entityManager;
    private final UsersRepository usersRepository;
    public OrdersService(OrdersRepository ordersRepository,
                         ProductsRepository productsRepository,
                         ProductOrdersRepository productOrdersRepository,
                         EntityManager entityManager,
                         UsersRepository usersRepository) {
        this.ordersRepository = ordersRepository;
        this.productsRepository = productsRepository;
        this.productOrdersRepository = productOrdersRepository;
        this.entityManager = entityManager;
        this.usersRepository = usersRepository;
    }
    public List<Order> getUserOrders(long userId){
        return ordersRepository.getUserOrders(userId);
    }
    public Optional<Order> placeOrder(Order order,String username){
        try {
            User mock = usersRepository.getUserByUsername(username);
            order.setUser(mock);
            entityManager.persist(mock);
            return Optional.of(ordersRepository.save(order));
        } catch (Exception e) {
            return Optional.of(new Order(0L));
        }
    }
    public void saveProductOrders(List<ProductOrder> products){
        try {
            productOrdersRepository.saveAll(products);
        } catch (Exception e) {
            System.out.println(e.getMessage());
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
