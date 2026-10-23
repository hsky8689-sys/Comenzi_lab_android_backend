package facultate.android_backend.Controller.Observer;

import facultate.android_backend.DTO.ProductDto;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class NotificationsController {
    @MessageMapping("/products/additions")
    @SendTo("/products")
    public ProductOperation handleAddition(ProductDto dto){
        return new ProductOperation(dto,CrudOperation.ADDITION);
    }
    @MessageMapping("/products/removals")
    @SendTo("/products")
    public ProductOperation handleDelete(ProductDto dto){
        return new ProductOperation(dto,CrudOperation.DELETION);
    }
    @MessageMapping("/orders")
    @SendToUser("/orders/placements")
    public String handleOrderPlacement(@Payload long id, Principal user){
        return String.valueOf(id);
    }
}
