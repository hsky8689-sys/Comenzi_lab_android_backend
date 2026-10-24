package facultate.android_backend.Controller;

import facultate.android_backend.DTO.DtoTranslator;
import facultate.android_backend.DTO.OrderDto;
import facultate.android_backend.Domain.Order;
import facultate.android_backend.Domain.ProductOrder;
import facultate.android_backend.Domain.ProductOrderKey;
import facultate.android_backend.Service.OrdersService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/orders")
public class OrdersController {
    private final OrdersService ordersService;
    @Autowired
    private WebSocketStompClient stompClient;
    public OrdersController(OrdersService orderSrv,
                            WebSocketStompClient stompClient){
        ordersService = orderSrv;
        this.stompClient = stompClient;
    }
    @Transactional
    @PostMapping("")
    public Object handleOrderPlacement(@RequestBody OrderDto order,
                                       @AuthenticationPrincipal UserDetails principal){
        final String username = principal.getUsername();
        final Order obtained = DtoTranslator.getFromDto(order);
        final Optional<Order> placed =  ordersService.placeOrder(DtoTranslator.getFromDto(order),username);
        if(placed.get().getId()==0)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(Map.of("message","could not add order"));
        final long idPlaced =placed.get().getId();
        List<ProductOrder> appearences =placed.get().getProductAppearences();
        appearences.forEach(a->{
            a.setId(new ProductOrderKey(idPlaced,a.getProduct().getId()));
            a.getOrder().setId(idPlaced);
        });
        ordersService.saveProductOrders(appearences);
        return ResponseEntity.ok(Map.of("message","Order has been succesfully added","id",idPlaced));
    }
}
