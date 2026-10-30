package facultate.android_backend.Controller;

import facultate.android_backend.DTO.DtoFactory;
import facultate.android_backend.DTO.RestaurantDto;
import facultate.android_backend.Service.RestaurantSearchField;
import facultate.android_backend.Service.RestaurantsService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/restaurants")
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
    // GET /restaurants/search?by=name&value=pizza&page=0&size=10   or   ?by=id&value=3
    @GetMapping("/search")
    public Object handleSearch(@RequestParam String by,
                               @RequestParam String value,
                               @PageableDefault(size = 10, sort = "name") Pageable pageable) {
        final RestaurantSearchField field;
        try {
            field = RestaurantSearchField.valueOf(by.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Search field must be 'name' or 'id'"));
        }
        try {
            final Page<RestaurantDto> found = restaurantsService.search(field, value.trim(), pageable);
            return ResponseEntity.ok(DtoFactory.makePageResponse("Restaurants found", "restaurants", found));
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Id must be a number"));
        }
    }
    @GetMapping
    public Object handleIdSearch(@RequestParam long id) {
        return restaurantsService.findById(id)
                .map(r -> ResponseEntity.ok(Map.<String, Object>of(
                        "message", "Restaurant found successfully",
                        "data", DtoFactory.makeRestaurantDto(r))))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Restaurant with id " + id + " was not found")));
    }
}
