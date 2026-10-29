package facultate.android_backend.Config;

import facultate.android_backend.DTO.ProductDto;
import facultate.android_backend.DTO.RestaurantDto;
import facultate.android_backend.Domain.Order;
import facultate.android_backend.Domain.Product;
import facultate.android_backend.Domain.ProductOrder;
import facultate.android_backend.Domain.ProductOrderKey;
import facultate.android_backend.Domain.User;
import facultate.android_backend.Repository.OrdersRepository;
import facultate.android_backend.Repository.ProductOrdersRepository;
import facultate.android_backend.Repository.ProductsRepository;
import facultate.android_backend.Repository.UsersRepository;
import facultate.android_backend.Service.RestaurantsService;
import facultate.android_backend.Service.UsersService;
import jakarta.transaction.Transactional;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@Component
public class DataSeeder implements ApplicationRunner {
    private final RestaurantsService restaurantsService;
    private final UsersService usersService;
    private final UsersRepository usersRepository;
    private final ProductsRepository productsRepository;
    private final OrdersRepository ordersRepository;
    private final ProductOrdersRepository productOrdersRepository;
    private final PasswordEncoder passwordEncoder;
    private final Random random = new Random(42);

    public DataSeeder(RestaurantsService restaurantsService,
                      UsersService usersService,
                      UsersRepository usersRepository,
                      ProductsRepository productsRepository,
                      OrdersRepository ordersRepository,
                      ProductOrdersRepository productOrdersRepository,
                      PasswordEncoder passwordEncoder) {
        this.restaurantsService = restaurantsService;
        this.usersService = usersService;
        this.usersRepository = usersRepository;
        this.productsRepository = productsRepository;
        this.ordersRepository = ordersRepository;
        this.productOrdersRepository = productOrdersRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (usersRepository.count() > 0) return;

        User radu = user("radu", "OWNER,CUSTOMER");
        User ana = user("ana", "CUSTOMER");
        User mihai = user("mihai", "CUSTOMER");
        user("ioana", "CUSTOMER");
        user("andrei", "OWNER");

        seedRestaurants();

        Map<Long, List<Product>> menus = productsRepository.findAll().stream()
                .collect(Collectors.groupingBy(p -> p.getRestaurant().getId()));
        List<List<Product>> restaurantMenus = new ArrayList<>(menus.values());

        seedOrders(radu, 30, restaurantMenus);
        seedOrders(ana, 12, restaurantMenus);
        seedOrders(mihai, 4, restaurantMenus);
    }

    private User user(String username, String roles) {
        return usersService.signup(User.builder()
                .username(username)
                .password(passwordEncoder.encode("pass"))
                .roles(roles)
                .build()).orElseThrow();
    }

    private void seedRestaurants() {
        restaurant("chioreana", "Vivo mall",
                product("burger", 10),
                product("cola", 4),
                product("borsec", 12),
                product("pizza", 32));
        restaurant("kfc", "Iulius mall",
                product("burger", 12),
                product("cola", 7),
                product("pui", 16),
                product("cartofi", 12),
                product("sandwich", 20),
                product("cacao", 12));
        restaurant("soviet", "centru cluj",
                product("stalinskaya", 30),
                product("cognac", 40),
                product("narghilea", 20),
                product("socata", 35));
        restaurant("pizzeria napoli", "strada horea 14",
                product("margherita", 28),
                product("quattro formaggi", 36),
                product("quattro stagioni", 35),
                product("diavola", 34),
                product("capricciosa", 33),
                product("prosciutto e funghi", 32),
                product("tonno", 34),
                product("vegetariana", 30),
                product("calzone", 36),
                product("carbonara", 37),
                product("hawaii", 33),
                product("marinara", 26),
                product("bianca", 31),
                product("salami", 32),
                product("frutti di mare", 42),
                product("focaccia", 15),
                product("bruschete", 18),
                product("tiramisu", 20),
                product("panna cotta", 18),
                product("limonada", 12),
                product("apa plata", 6),
                product("bere", 10));
        restaurant("la mama", "bulevardul eroilor 3",
                product("ciorba de burta", 22),
                product("ciorba de perisoare", 20),
                product("sarmale cu mamaliga", 32),
                product("mici", 18),
                product("tochitura", 38),
                product("snitel de pui", 30),
                product("piure", 10),
                product("salata de varza", 8),
                product("papanasi", 24),
                product("clatite", 18),
                product("must", 9));
        restaurant("sushi bar", "strada memorandumului 21",
                product("california roll", 32),
                product("philadelphia roll", 36),
                product("spicy tuna roll", 38),
                product("nigiri somon", 26),
                product("sashimi mix", 52),
                product("supa miso", 14),
                product("edamame", 16),
                product("ramen", 40),
                product("ceai verde", 8),
                product("mochi", 15));
        restaurant("shaormeria centrala", "piata mihai viteazu 5",
                product("shaorma mica", 22),
                product("shaorma mare", 28),
                product("shaorma vita", 32),
                product("falafel", 24),
                product("cartofi prajiti", 10),
                product("ayran", 7),
                product("baklava", 12));
        restaurant("cafeneaua veche", "strada universitatii 7",
                product("espresso", 9),
                product("cappuccino", 13),
                product("latte", 14),
                product("flat white", 15),
                product("ciocolata calda", 14),
                product("croissant", 10),
                product("cheesecake", 19),
                product("briosa", 9),
                product("fresh portocale", 16));
    }

    private void restaurant(String name, String adress, ProductDto... products) {
        RestaurantDto dto = new RestaurantDto();
        dto.setName(name);
        dto.setAdress(adress);
        dto.setProducts(List.of(products));
        restaurantsService.addRestaurant(dto);
    }

    private static ProductDto product(String name, long price) {
        ProductDto dto = new ProductDto();
        dto.setName(name);
        dto.setPrice(price);
        return dto;
    }

    private void seedOrders(User user, int count, List<List<Product>> restaurantMenus) {
        for (int i = 0; i < count; i++) {
            Order order = ordersRepository.save(Order.builder()
                    .user(user)
                    .orderTime(LocalDateTime.now()
                            .minusDays(random.nextInt(90))
                            .minusMinutes(random.nextInt(24 * 60)))
                    .build());

            List<Product> menu = new ArrayList<>(restaurantMenus.get(random.nextInt(restaurantMenus.size())));
            Collections.shuffle(menu, random);
            int lines = 1 + random.nextInt(Math.min(4, menu.size()));

            List<ProductOrder> appearences = new ArrayList<>();
            for (Product product : menu.subList(0, lines)) {
                ProductOrder line = new ProductOrder();
                line.setId(new ProductOrderKey(order.getId(), product.getId()));
                line.setOrder(order);
                line.setProduct(product);
                line.setQuantityOrdered(1 + random.nextInt(3));
                line.setPricePerProduct(product.getPrice());
                appearences.add(line);
            }
            productOrdersRepository.saveAll(appearences);
        }
    }
}
