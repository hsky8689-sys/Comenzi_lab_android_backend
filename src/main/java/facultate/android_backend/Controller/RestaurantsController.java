package facultate.android_backend.Controller;

import facultate.android_backend.DTO.DtoFactory;
import facultate.android_backend.DTO.RestaurantDto;
import facultate.android_backend.Service.RestaurantsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/restaurants")
@CrossOrigin(origins = "*")
public class RestaurantsController {
    private final RestaurantsService restaurantsService;
    public RestaurantsController(RestaurantsService restaurantsService) {
        this.restaurantsService = restaurantsService;
    }
    @DeleteMapping("/{id}")
    public Object handleDeletion(@PathVariable("id") Long id){
        return restaurantsService.deleteRestaurant(id) == id
                ?
                ResponseEntity.ok().body(Map.of("message","Restaurant succesfully deleted"))
                :ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message","Restaurant not found"));
    }
    @PostMapping
    public Object handleAddition(@RequestBody RestaurantDto dto){
        return restaurantsService.addRestaurant(dto)
                .map(r -> ResponseEntity.ok(Map.<String, Object>of(
                        "message", "Restaurant found successfully",
                        "data", DtoFactory.makeRestaurantDto(r))))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", new StringBuilder("Restaurant with id ")
                                                    .append(dto.getId().toString())
                                                   .append(" was not added").toString())));
    }
    @GetMapping
    public ResponseEntity<Map<String, Object>> handleIdSearch(@RequestParam long id) {
        return restaurantsService.findById(id)
                .map(r -> ResponseEntity.ok(Map.<String, Object>of(
                        "message", "Restaurant found successfully",
                        "data", DtoFactory.makeRestaurantDto(r))))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Restaurant with id " + id + " was not found")));
    }
}
