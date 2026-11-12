package facultate.android_backend.DTO;

import facultate.android_backend.Domain.Order;
import facultate.android_backend.Domain.Product;
import facultate.android_backend.Domain.ProductOrder;
import facultate.android_backend.Domain.Restaurant;

import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

public class DtoFactory {
    // stable JSON for pages (serializing PageImpl directly is not guaranteed to keep its shape)
    public static Map<String, Object> makePageResponse(String message, String itemsKey, Page<?> page){
        return Map.of(
                "message", message,
                itemsKey, page.getContent(),
                "page", page.getNumber(),
                "size", page.getSize(),
                "totalPages", page.getTotalPages(),
                "totalElements", page.getTotalElements(),
                "last", page.isLast());
    }
    //doar in metode transactional ca da trigger la fetching....
    public static RestaurantDto makeRestaurantDto(Restaurant restaurant){
        var answer = new RestaurantDto();
        if(restaurant==null)return answer;
        answer.setId(restaurant.getId());
        answer.setName(restaurant.getName());
        answer.setAdress(restaurant.getAdress());
        answer.setImage(restaurant.getImage());
        answer.setProducts(restaurant.getProductList().stream()
                                     .map(DtoFactory::makeProductDto)
                                     .collect(Collectors.toCollection(ArrayList::new))
        );
        return answer;
    }
    public static ProductDto makeProductDto(Product product){
        var answer = new ProductDto();
        if(product==null)return answer;
        answer.setId(product.getId());
        answer.setName(product.getName());
        answer.setPrice(product.getPrice());
        answer.setImage(product.getImage());
        answer.setRestaurant(product.getRestaurant().getId());
        return answer;
    }
    // produsul asa cum era in comanda: cantitatea si pretul salvate in ProductOrder,
    // nu pretul curent din meniu (care se poate schimba intre timp)
    public static ProductDto makeOrderedProductDto(ProductOrder line){
        var answer = makeProductDto(line.getProduct());
        answer.setPrice(line.getPricePerProduct());
        answer.setQuantity(line.getQuantityOrdered());
        return answer;
    }
    //doar in metode transactional ca da trigger la fetching.... x2
    public static OrderDto makeOrderDto(Order order){
        var answer = new OrderDto();
        if(order==null)return answer;
        answer.setId(order.getId());
        answer.setOrderTime(order.getOrderTime());
        answer.setProducts(order.getProductAppearences()
                                .stream().map(DtoFactory::makeOrderedProductDto)
                                .collect(Collectors.toCollection(ArrayList::new)));
        return answer;
    }
}
