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
    public RestaurantsService(RestaurantsRepository restaurantsRepo,
                              ProductsRepository productsRepository,
                              UsersRepository usersRepository, EntityManager entityManager){
        restaurantsRepository = restaurantsRepo;
        this.productsRepository = productsRepository;
        this.usersRepository = usersRepository;
        this.entityManager = entityManager;
    }
    @Transactional
    public long deleteRestaurant(Long id){
        return restaurantsRepository.deleteRestaurant(id) !=0 ? id : 0;
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
        if(!Objects.equals(owner.get().getId(), dto.getOwnerId())){
            throw new RuntimeException("Cannot add restaurant to different user");
        }
        Restaurant added = DtoTranslator.getFromDto(dto);
        added.setOwner(owner.get());
        List<Product> newProducts = dto.getProducts().stream()
                        .map(DtoTranslator::getFromDto)
                        .collect(Collectors.toCollection(ArrayList::new));
        restaurantsRepository.save(added);
        newProducts.forEach(p -> p.setRestaurant(added));
        productsRepository.saveAll(newProducts);
        return Optional.of(added);
    }
}
