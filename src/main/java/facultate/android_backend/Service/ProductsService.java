package facultate.android_backend.Service;

import facultate.android_backend.Controller.Observer.CrudOperation;
import facultate.android_backend.Controller.Observer.ProductOperation;
import facultate.android_backend.DTO.DtoTranslator;
import facultate.android_backend.DTO.ProductDto;
import facultate.android_backend.Domain.Product;
import facultate.android_backend.Repository.ProductsRepository;
import jakarta.transaction.Transactional;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProductsService {
    private final ProductsRepository productsRepository;
    private final SimpMessagingTemplate messagingTemplate;
    public ProductsService(ProductsRepository productsRepository,
                           SimpMessagingTemplate messagingTemplate){
        this.productsRepository = productsRepository;
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
    public long deleteProduct(ProductDto dto){
        try {
            long result = productsRepository.deleteProduct(dto.getId()) != 0 ? dto.getId() : 0;
            dto.setId(result);
            messagingTemplate.convertAndSend("/products",new ProductOperation(dto, CrudOperation.DELETION));
            return result;
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return 0;
        }
    }
}
