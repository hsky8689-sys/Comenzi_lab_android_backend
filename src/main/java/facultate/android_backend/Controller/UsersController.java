package facultate.android_backend.Controller;

import facultate.android_backend.DTO.DtoTranslator;
import facultate.android_backend.DTO.LoginRequest;
import facultate.android_backend.DTO.SignupRequest;
import facultate.android_backend.Service.JwtService;
import facultate.android_backend.Service.OrdersService;
import facultate.android_backend.Service.UsersService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*")
public class UsersController {
    private UsersService usersService;
    private OrdersService ordersService;
   private final JwtService jwtService;
   private final AuthenticationManager authenticationManager;
    public UsersController(UsersService service,
                           OrdersService ordersService,
                           JwtService jwtService,
                           AuthenticationManager authenticationManager){
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        ordersService = ordersService;
        usersService = service;
    }
    @PostMapping("/login")
    public Object handleLogin(@RequestBody LoginRequest request){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(),request.getPassword())
        );
        return usersService.login(request.getUsername(), request.getPassword())
                .map(r->
                    ResponseEntity.ok(Map.of(
                       "message","User succesfully logged in",
                       "jwt", jwtService.generateToken(request.getUsername()),
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
