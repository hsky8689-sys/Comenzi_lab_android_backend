package facultate.android_backend.DTO;

import facultate.android_backend.Domain.*;

import java.util.Objects;

public class DtoTranslator {
    public static Restaurant getFromDto(RestaurantDto dto){
        Restaurant answer = new Restaurant();
        if(dto==null)return answer;
        answer.setId(dto.getId());
        answer.setAdress(dto.getAdress());
        answer.setName(dto.getName());
        return answer;
    }
    public static Product getFromDto(ProductDto dto){
        Product answer = new Product();
        if(dto==null)return answer;
        answer.setId(dto.getId());
        answer.setName(dto.getName());
        answer.setPrice(dto.getPrice());
        Restaurant restaurant = new Restaurant();
        restaurant.setId(dto.getRestaurant());
        answer.setRestaurant(restaurant);
        return answer;
    }
    public static Order getFromDto(OrderDto dto){
        Order answer = new Order();
        if(dto==null)return answer;
        answer.setId(dto.getId());
        answer.setOrderTime(dto.getOrderTime());
        User user = new User();
        answer.setUser(user);
        answer.setProductAppearences(dto.getProducts().stream()
                        .map(r->new ProductOrder(new ProductOrderKey(0L,r.getId())
                                , new Product(r.getId())
                                , new Order(0L)
                                ,dto.getProducts().stream()
                                .map(ProductDto::getId)
                                .filter(c-> Objects.equals(c, r.getId()))
                                .toList()
                                .size(),
                                r.getPrice())
                        ).toList()
        );
        return answer;
    }
    public static User getFromDto(SignupRequest dto){
        User answer = new User();
        if(dto==null)return answer;
        answer.setId(null);
        answer.setPassword(dto.getPassword());
        answer.setUsername(dto.getUsername());
        return answer;
    }
}