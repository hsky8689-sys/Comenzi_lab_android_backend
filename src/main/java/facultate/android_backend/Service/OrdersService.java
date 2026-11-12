package facultate.android_backend.Service;

import facultate.android_backend.DTO.DtoFactory;
import facultate.android_backend.DTO.DtoTranslator;
import facultate.android_backend.DTO.OrderDto;
import facultate.android_backend.Domain.Order;
import facultate.android_backend.Domain.ProductOrder;
import facultate.android_backend.Domain.User;
import facultate.android_backend.Repository.OrdersRepository;
import facultate.android_backend.Repository.ProductOrdersRepository;
import facultate.android_backend.Repository.ProductsRepository;
import facultate.android_backend.Repository.UsersRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
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
    public Page<Order> getUserOrders(String username, Pageable pageable){
        return ordersRepository.findByUser_Username(username,pageable);
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
    public Optional<Order> findByIdempotencyKey(String idempotencyKey, String username){
        return ordersRepository.findByIdempotencyKeyAndUser_Username(idempotencyKey, username);
    }
    public void saveProductOrders(List<ProductOrder> products){
        productOrdersRepository.saveAll(products);
    }/*
    @Transactional
    public long computeTotalAmountSpent(long userId){
        return getUserOrders(userId).stream()
                             .map(o->
                                     (Long) o.getProductAppearences().stream()
                                             .map(pa -> pa.getPricePerProduct() * pa.getQuantityOrdered())
                                             .mapToLong(Long::longValue).sum()
                            ).mapToLong(Long::longValue).sum();
    }*/
    public long computeOrderCosts(long orderId){
        return ordersRepository.computeOrderTotalCost(orderId);
    }
    public Optional<OrderDto> getOrderDetails(Long orderId, String username) {
        Order found = ordersRepository.getOrderByIdAndUser_UsernameAfter(orderId,username);
        if(found!=null){
            Long price = computeOrderCosts(found.getId());
            OrderDto dto = DtoFactory.makeOrderDto(found);
            dto.setTotalPrice(price);
            return Optional.of(dto);
        }
        return Optional.empty();
    }
}
