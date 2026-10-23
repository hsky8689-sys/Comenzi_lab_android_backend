package facultate.android_backend.Service;

import facultate.android_backend.DTO.DtoTranslator;
import facultate.android_backend.DTO.RestaurantDto;
import facultate.android_backend.Domain.Product;
import facultate.android_backend.Domain.Restaurant;
import facultate.android_backend.Repository.ProductsRepository;
import facultate.android_backend.Repository.RestaurantsRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RestaurantsService {
    private RestaurantsRepository restaurantsRepository;
    private ProductsRepository productsRepository;
    public RestaurantsService(RestaurantsRepository restaurantsRepo,
                              ProductsRepository productsRepository){
        restaurantsRepository = restaurantsRepo;
        this.productsRepository = productsRepository;
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
    public Optional<Restaurant> addRestaurant(RestaurantDto dto){
            try {
                Restaurant added = DtoTranslator.getFromDto(dto);
                List<Product> newProducts = dto.getProducts().stream()
                        .map(DtoTranslator::getFromDto)
                        .collect(Collectors.toCollection(ArrayList::new));
                restaurantsRepository.save(added);
                newProducts.forEach(p -> p.setRestaurant(added));
                productsRepository.saveAll(newProducts);
                return Optional.of(added);
            } catch (Exception e) {
                System.err.println(e.getMessage());
                return Optional.empty();
            }
    }
}
