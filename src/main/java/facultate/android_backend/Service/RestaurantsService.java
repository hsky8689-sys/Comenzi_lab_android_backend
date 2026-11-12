package facultate.android_backend.Service;

import facultate.android_backend.DTO.DtoFactory;
import facultate.android_backend.DTO.DtoTranslator;
import facultate.android_backend.DTO.RestaurantDto;
import facultate.android_backend.Domain.Product;
import facultate.android_backend.Domain.Restaurant;
import facultate.android_backend.Domain.User;
import facultate.android_backend.Repository.ProductsRepository;
import facultate.android_backend.Repository.RestaurantsRepository;
import facultate.android_backend.Repository.UsersRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RestaurantsService {
    private final RestaurantsRepository restaurantsRepository;
    private final ProductsRepository productsRepository;
    private final UsersRepository usersRepository;
    private final EntityManager entityManager;
    private final ImageStorage imageStorage;
    public RestaurantsService(RestaurantsRepository restaurantsRepo,
                              ProductsRepository productsRepository,
                              UsersRepository usersRepository, EntityManager entityManager,
                              ImageStorage imageStorage){
        restaurantsRepository = restaurantsRepo;
        this.productsRepository = productsRepository;
        this.usersRepository = usersRepository;
        this.entityManager = entityManager;
        this.imageStorage = imageStorage;
    }
    // restaurantul doar daca exista si e al utilizatorului logat; altfel empty (controller-ul da 404 in ambele cazuri)
    public Optional<Restaurant> findOwned(long id, String username){
        return restaurantsRepository.findById(id)
                .filter(r -> r.getOwner() != null && r.getOwner().getUsername().equals(username));
    }
    @Transactional
    public boolean deleteRestaurant(Long id, String username){
        return findOwned(id, username)
                .map(r -> restaurantsRepository.deleteRestaurant(id) != 0)
                .orElse(false);
    }
    @Transactional
    public Optional<RestaurantDto> updateRestaurant(long id, RestaurantDto dto, String username){
        return findOwned(id, username).map(r -> {
            // modificam entitatea gestionata, Hibernate face UPDATE la commit (dirty checking)
            if (dto.getName() != null) r.setName(dto.getName());
            if (dto.getAdress() != null) r.setAdress(dto.getAdress());
            return DtoFactory.makeRestaurantDto(r);
        });
    }
    @Transactional
    public Optional<RestaurantDto> setImage(long id, MultipartFile file, String username) throws IOException {
        Optional<Restaurant> found = findOwned(id, username);
        if (found.isEmpty()) return Optional.empty();
        found.get().setImage(imageStorage.store(file, "restaurants", "restaurant-" + id));
        return found.map(DtoFactory::makeRestaurantDto);
    }
    public List<Product> getRestaurantMenu(long id){
        return restaurantsRepository.getAllProducts(id);
    }
    public Optional<Restaurant> findById(long id){
        return restaurantsRepository.findById(id);
    }
    @Transactional
    public Page<RestaurantDto> search(RestaurantSearchField by, String value, Pageable pageable){
        Page<Restaurant> found = switch (by) {
            case NAME -> restaurantsRepository.findByNameContainingIgnoreCase(value, pageable);
            case ID -> restaurantsRepository.findByIdEquals(Long.parseLong(value), pageable);
        };
        return found.map(DtoFactory::makeRestaurantDto);
    }
    public Page<RestaurantDto> getRestaurantsByUsername(String username,Pageable page){
        try{
            var restaurant= restaurantsRepository.findByOwnerUsername(username,page);
            return restaurant.map(DtoFactory::makeRestaurantDto);
        } catch (Exception e) {
            return Page.empty();
        }
    }
    @Transactional
    public Optional<Restaurant> addRestaurant(RestaurantDto dto,String ownerUsername){
        Optional<User> owner = usersRepository.findByUsername(ownerUsername);
        if(owner.isEmpty()){
            throw new RuntimeException("Restaurant owner was not found");
        }
        // owner-ul vine din token, nu din body: clientul nu poate alege al cui e restaurantul
        Restaurant added = DtoTranslator.getFromDto(dto);
        added.setId(null);
        added.setOwner(owner.get());
        List<Product> newProducts = Optional.ofNullable(dto.getProducts()).orElse(List.of()).stream()
                        .map(DtoTranslator::getFromDto)
                        .collect(Collectors.toCollection(ArrayList::new));
        restaurantsRepository.save(added);
        newProducts.forEach(p -> p.setRestaurant(added));
        productsRepository.saveAll(newProducts);
        return Optional.of(added);
    }
}
