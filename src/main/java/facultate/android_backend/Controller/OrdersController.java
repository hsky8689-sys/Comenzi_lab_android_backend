package facultate.android_backend.Controller;

import facultate.android_backend.DTO.DtoTranslator;
import facultate.android_backend.DTO.OrderDto;
import facultate.android_backend.Service.OrdersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.util.Map;

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
    @PostMapping("")
    public Object handleOrderPlacement(@RequestBody OrderDto order){
        order.setId(null);
        return ordersService.placeOrder(DtoTranslator.getFromDto(order))
                .map(r-> {
                    return ResponseEntity.status(HttpStatus.OK)
                            .body(Map.of("message","Order successfully placed","id",r.getId()));
                })
                .orElseGet(()->{
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(Map.of("message","Could not place order"));
                });
    }
}
