package facultate.android_backend.Service;

import facultate.android_backend.Controller.Observer.CrudOperation;
import facultate.android_backend.Controller.Observer.ProductOperation;
import facultate.android_backend.DTO.DtoFactory;
import facultate.android_backend.DTO.DtoTranslator;
import facultate.android_backend.DTO.ProductDto;
import facultate.android_backend.Domain.Product;
import facultate.android_backend.Domain.Restaurant;
import facultate.android_backend.Repository.ProductsRepository;
import facultate.android_backend.Repository.RestaurantsRepository;
import jakarta.transaction.Transactional;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import java.util.Optional;

@Service
public class ProductsService {
    private final ProductsRepository productsRepository;
    private final RestaurantsRepository restaurantsRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final RestaurantsService restaurantsService;
    private final ImageStorage imageStorage;
    public ProductsService(ProductsRepository productsRepository,
                           RestaurantsRepository restaurantsRepository,
                           SimpMessagingTemplate messagingTemplate,
                           RestaurantsService restaurantsService,
                           ImageStorage imageStorage){
        this.productsRepository = productsRepository;
        this.restaurantsRepository = restaurantsRepository;
        this.messagingTemplate = messagingTemplate;
        this.restaurantsService = restaurantsService;
        this.imageStorage = imageStorage;
    }
    // produsul doar daca restaurantul lui e al utilizatorului logat
    public Optional<Product> findOwned(long id, String username){
        return productsRepository.findById(id)
                .filter(p -> restaurantsService.findOwned(p.getRestaurant().getId(), username).isPresent());
    }
    @Transactional
    public Optional<Product> addProduct(ProductDto dto, String username){
        Optional<Restaurant> restaurant = restaurantsService.findOwned(dto.getRestaurant(), username);
        if (restaurant.isEmpty()) return Optional.empty();
        Product product = DtoTranslator.getFromDto(dto);
        product.setId(null);
        product.setRestaurant(restaurant.get());
        var added = Optional.of(productsRepository.save(product));
        try {
            dto.setId(added.map(Product::getId).orElse(null));
            messagingTemplate.convertAndSend("/products", new ProductOperation(dto, CrudOperation.ADDITION));
        } catch (MessagingException e) {
            System.err.println(e.getMessage());
            return added;
        }
        return added;
    }
    @Transactional
    public Optional<ProductDto> updateProduct(long id, ProductDto dto, String username){
        return findOwned(id, username).map(p -> {
            if (dto.getName() != null) p.setName(dto.getName());
            if (dto.getPrice() > 0) p.setPrice(dto.getPrice());
            return DtoFactory.makeProductDto(p);
        });
    }
    @Transactional
    public Optional<ProductDto> setImage(long id, MultipartFile file, String username) throws IOException {
        Optional<Product> found = findOwned(id, username);
        if (found.isEmpty()) return Optional.empty();
        found.get().setImage(imageStorage.store(file, "products", "product-" + id));
        return found.map(DtoFactory::makeProductDto);
    }
    @Transactional
    public boolean deleteProduct(long id, String ownerUsername){
        Optional<ProductDto> deleted = findOwned(id, ownerUsername).map(DtoFactory::makeProductDto);
        if (deleted.isEmpty() || productsRepository.deleteProduct(id) == 0) return false;
        try {
            messagingTemplate.convertAndSend("/products", new ProductOperation(deleted.get(), CrudOperation.DELETION));
        } catch (MessagingException e) {
            System.err.println(e.getMessage());
        }
        return true;
    }
}
