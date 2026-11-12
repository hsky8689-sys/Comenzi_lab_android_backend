package facultate.android_backend.Controller;

import facultate.android_backend.DTO.ProductDto;
import facultate.android_backend.Service.ProductsService;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/products")
public class ProductsController {
    private ProductsService productsService;
    public ProductsController(ProductsService prodService){
        productsService = prodService;
    }
    @DeleteMapping("/{id}")
    public Object handleDeletion(@PathVariable("id") long id){
        var dto = new ProductDto();
        dto.setId(id);
        return productsService.deleteProduct(dto) == id
                ? ResponseEntity.ok(Map.of(
                            "productId",id,
                        "message","Product succesfuly deleted"
                 )):
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("productId",id,"message","Product could not be deleted"));
    }
    @PostMapping
    @Transactional
    public Object handleAddition(@RequestBody ProductDto dto){
        return productsService.addProduct(dto)
                .map(r->{
                    return ResponseEntity.ok().body(Map.of(
                            "message","Product succesfully added",
                            "id",r.getId(),
                            "restaurant_id",r.getRestaurant().getId(),
                            "price",r.getPrice()
                    ));
                }).orElseGet(
                        () -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(Map.of("message","Could not add product"))
                );
    }
}
