package facultate.android_backend.DTO;

import facultate.android_backend.Domain.Order;
import facultate.android_backend.Domain.Product;
import facultate.android_backend.Domain.Restaurant;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class DtoFactory {
    //doar in metode transactional ca da trigger la fetching....
    public static RestaurantDto makeRestaurantDto(Restaurant restaurant){
        var answer = new RestaurantDto();
        if(restaurant==null)return answer;
        answer.setId(restaurant.getId());
        answer.setName(restaurant.getName());
        answer.setAdress(restaurant.getAdress());
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
        answer.setRestaurant(product.getRestaurant().getId());
        return answer;
    }
    //doar in metode transactional ca da trigger la fetching.... x2
    public static OrderDto makeOrderDto(Order order){
        var answer = new OrderDto();
        if(order==null)return answer;
        answer.setId(order.getId());
        answer.setOrderTime(order.getOrderTime());
        answer.setUserId(order.getUser().getId());
        answer.setProducts(order.getProductAppearences()
                                .stream().map(po->DtoFactory.makeProductDto(po.getProduct())
                          ).collect(Collectors.toCollection(ArrayList::new)));
        return answer;
    }
}
