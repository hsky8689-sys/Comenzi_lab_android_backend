package facultate.android_backend.Controller;

import facultate.android_backend.DTO.DtoTranslator;
import facultate.android_backend.DTO.LoginRequest;
import facultate.android_backend.DTO.SignupRequest;
import facultate.android_backend.Service.JwtService;
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
public class UsersController {
    private final UsersService usersService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    public UsersController(UsersService service,
                           JwtService jwtService,
                           AuthenticationManager authenticationManager){
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        usersService = service;
    }
    @PostMapping("/login")
    public Object handleLogin(@RequestBody LoginRequest request){
        Authentication authentication = null;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
        } catch (Exception e) {
            System.out.println(e.getMessage());
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
        if(authentication!=null) {
            return authentication.isAuthenticated()
                    ?
                    ResponseEntity.ok(Map.of(
                            "message", "User succesfully logged in",
                            "jwt", jwtService.generateToken(request.getUsername())
                    ))
                    : ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Bad credentials"));
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message","Internal server error"));
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
