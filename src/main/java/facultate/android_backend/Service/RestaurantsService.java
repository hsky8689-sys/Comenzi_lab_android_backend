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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Comparator;
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
            if (dto.getName() != null) r.setName(dto.getName());
            if (dto.getAdress() != null) r.setAdress(dto.getAdress());
            if (dto.getLocation() != null) r.setLocation(DtoTranslator.getFromDto(dto.getLocation()));
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
    // distanta "in linie dreapta" pe suprafata Pamantului intre doua puncte (formula haversine), in km
    static double distanceKm(double lat1, double lng1, double lat2, double lng2){
        final double earthRadiusKm = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.pow(Math.sin(dLat / 2), 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.pow(Math.sin(dLng / 2), 2);
        return 2 * earthRadiusKm * Math.asin(Math.sqrt(a));
    }
    @Transactional
    public Page<RestaurantDto> nearby(double latitude, double longitude, double radiusKm, Pageable pageable){
        List<RestaurantDto> found = new ArrayList<>();
        for (Restaurant r : restaurantsRepository.findAll()) {
            if (r.getLocation() == null) continue;
            double distance = distanceKm(latitude, longitude,
                    r.getLocation().getLatitude(), r.getLocation().getLongitude());
            if (distance <= radiusKm) {
                RestaurantDto dto = DtoFactory.makeRestaurantDto(r);
                dto.setDistanceKm(Math.round(distance * 100) / 100.0);
                found.add(dto);
            }
        }
        found.sort(Comparator.comparing(RestaurantDto::getDistanceKm));
        int from = (int) Math.min(pageable.getOffset(), found.size());
        int to = Math.min(from + pageable.getPageSize(), found.size());
        return new PageImpl<>(found.subList(from, to), pageable, found.size());
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
