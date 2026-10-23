package facultate.android_backend.Controller;

import facultate.android_backend.DTO.DtoFactory;
import facultate.android_backend.DTO.DtoTranslator;
import facultate.android_backend.DTO.LoginRequest;
import facultate.android_backend.DTO.SignupRequest;
import facultate.android_backend.Domain.Order;
import facultate.android_backend.Service.OrdersService;
import facultate.android_backend.Service.UsersService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static tools.jackson.databind.cfg.CoercionInputShape.Array;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*")
public class UsersController {
    private UsersService usersService;
    private OrdersService ordersService;
    public UsersController(UsersService service,OrdersService ordersService){
        ordersService = ordersService;
        usersService = service;
    }
    @PostMapping("/login")
    public Object handleLogin(@RequestBody LoginRequest request){
        return usersService.login(request.getUsername(), request.getPassword())
                .map(r->
                    ResponseEntity.ok(Map.of(
                       "message","User succesfully logged in",
                       "id",  r.getId()
                    ))
                ).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Bad credentials")));
    }
    @PostMapping("/signup")
    public Object handleSignup(@RequestBody SignupRequest request){
        return usersService.signup(DtoTranslator.getFromDto(request))
                .map(u->ResponseEntity.ok(Map.of(
                        "message","User successfully created",
                        "id",u.getId()
                        ))
                ).orElseGet(()->ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("message","User wasn't created"))
                );
    }
}
