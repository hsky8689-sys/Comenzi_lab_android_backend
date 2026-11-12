package facultate.android_backend.Controller;

import facultate.android_backend.DTO.DtoFactory;
import facultate.android_backend.DTO.ProductDto;
import facultate.android_backend.Service.ProductsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/products")
public class ProductsController {
    private final ProductsService productsService;
    public ProductsController(ProductsService prodService){
        productsService = prodService;
    }
    private static ResponseEntity<Map<String, Object>> notFound(long id){
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("productId", id, "message", "Product not found"));
    }
    @DeleteMapping("/{id}")
    public Object handleDeletion(@AuthenticationPrincipal UserDetails userDetails
                                ,@PathVariable("id") long id){
        return productsService.deleteProduct(id, userDetails.getUsername())
                ? ResponseEntity.ok(Map.of("productId", id, "message", "Product succesfuly deleted"))
                : notFound(id);
    }
    @PostMapping
    public Object handleAddition(@RequestBody ProductDto dto,
                                 @AuthenticationPrincipal UserDetails userDetails){
        return productsService.addProduct(dto, userDetails.getUsername())
                .map(p -> ResponseEntity.ok(Map.<String, Object>of(
                        "message", "Product succesfully added",
                        "data", DtoFactory.makeProductDto(p))))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Restaurant not found")));
    }
    @PutMapping("/{id}")
    public Object handleUpdate(@PathVariable("id") long id,
                               @RequestBody ProductDto dto,
                               @AuthenticationPrincipal UserDetails userDetails){
        return productsService.updateProduct(id, dto, userDetails.getUsername())
                .map(p -> ResponseEntity.ok(Map.<String, Object>of("message", "Product updated", "data", p)))
                .orElseGet(() -> notFound(id));
    }
    @PostMapping("/{id}/image")
    public Object handleImage(@PathVariable("id") long id,
                              @RequestParam("file") MultipartFile file,
                              @AuthenticationPrincipal UserDetails userDetails){
        try {
            return productsService.setImage(id, file, userDetails.getUsername())
                    .map(p -> ResponseEntity.ok(Map.<String, Object>of("message", "Image saved", "data", p)))
                    .orElseGet(() -> notFound(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Could not save image"));
        }
    }
}
