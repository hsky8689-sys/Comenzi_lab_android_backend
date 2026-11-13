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
        answer.setImage(dto.getImage());
        answer.setLocation(getFromDto(dto.getLocation()));
        return answer;
    }
    // o locatie e valida doar cu ambele coordonate, in intervalele reale
    public static Location getFromDto(LocationDto dto){
        if (dto == null || dto.latitude() == null || dto.longitude() == null) return null;
        if (Math.abs(dto.latitude()) > 90 || Math.abs(dto.longitude()) > 180) return null;
        return new Location(dto.latitude(), dto.longitude());
    }
    public static Product getFromDto(ProductDto dto){
        Product answer = new Product();
        if(dto==null)return answer;
        answer.setId(dto.getId());
        answer.setName(dto.getName());
        answer.setPrice(dto.getPrice());
        answer.setImage(dto.getImage());
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
        answer.setIdempotencyKey(dto.getIdempotencyKey());
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
        answer.setEmail(dto.getEmail());
        return answer;
    }
}