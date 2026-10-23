package facultate.android_backend.Config;

import facultate.android_backend.DTO.ProductDto;
import facultate.android_backend.DTO.RestaurantDto;
import facultate.android_backend.Domain.User;
import facultate.android_backend.Repository.UsersRepository;
import facultate.android_backend.Service.RestaurantsService;
import facultate.android_backend.Service.UsersService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataSeeder implements ApplicationRunner {
    private final RestaurantsService restaurantsService;
    private final UsersService usersService;
    private final UsersRepository usersRepository;

    public DataSeeder(RestaurantsService restaurantsService,
                      UsersService usersService,
                      UsersRepository usersRepository) {
        this.restaurantsService = restaurantsService;
        this.usersService = usersService;
        this.usersRepository = usersRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (usersRepository.count() > 0) return;

        usersService.signup(User.builder()
                .username("radu")
                .password("pass")
                .build());

        RestaurantDto chioreana = new RestaurantDto();
        chioreana.setName("chioreana");
        chioreana.setAdress("Vivo mall");
        chioreana.setProducts(List.of(
                product("burger", 10),
                product("cola", 4),
                product("borsec", 12),
                product("pizza", 32)
        ));
        RestaurantDto kfc = new RestaurantDto();
        kfc.setName("kfc");
        kfc.setAdress("Iulius mall");
        kfc.setProducts(List.of(
                product("burger", 12),
                product("cola", 7),
                product("pui", 16),
                product("cartofi", 12),
                product("sandwich", 20),
                product("cacao", 12)
        ));
        RestaurantDto soviet = new RestaurantDto();
        soviet.setName("soviet");
        soviet.setAdress("centru cluj");
        soviet.setProducts(List.of(
                product("stalinskaya", 30),
                product("cognac", 40),
                product("narghilea", 20),
                product("socata", 35)
        ));
        restaurantsService.addRestaurant(chioreana);
        restaurantsService.addRestaurant(kfc);
        restaurantsService.addRestaurant(soviet);
    }

    private static ProductDto product(String name, long price) {
        ProductDto dto = new ProductDto();
        dto.setName(name);
        dto.setPrice(price);
        return dto;
    }
}
