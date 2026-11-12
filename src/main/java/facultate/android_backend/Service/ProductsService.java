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

import java.util.Optional;

@Service
public class ProductsService {
    private final ProductsRepository productsRepository;
    private final RestaurantsRepository restaurantsRepository;
    private final SimpMessagingTemplate messagingTemplate;
    public ProductsService(ProductsRepository productsRepository,
                           RestaurantsRepository restaurantsRepository,
                           SimpMessagingTemplate messagingTemplate){
        this.productsRepository = productsRepository;
        this.restaurantsRepository = restaurantsRepository;
        this.messagingTemplate = messagingTemplate;
    }
    public Optional<Product> addProduct(ProductDto dto){
        var added = Optional.of(productsRepository.save(DtoTranslator.getFromDto(dto)));
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
    public long deleteProduct(ProductDto dto,String ownerUsername){
        try {
            Optional<Restaurant> found = restaurantsRepository.findById(dto.getRestaurant());
            if(found.isEmpty()){
                throw new RuntimeException("No such restaurant found");
            }
            final String foundOwnerUsername = found.get().getOwner().getUsername();
            if(!foundOwnerUsername.equals(ownerUsername)){
                throw new RuntimeException("Operation not permitted");
            }
            Optional<ProductDto> deleted = productsRepository.findById(dto.getId()).map(DtoFactory::makeProductDto);
            long result = productsRepository.deleteProduct(dto.getId()) != 0 ? dto.getId() : 0;
            if(result != 0 && deleted.isPresent())
                messagingTemplate.convertAndSend("/products",new ProductOperation(deleted.get(), CrudOperation.DELETION));
            return result;
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return 0;
        }
    }
}
