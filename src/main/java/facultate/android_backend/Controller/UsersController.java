package facultate.android_backend.Controller;

import facultate.android_backend.DTO.DtoTranslator;
import facultate.android_backend.DTO.LoginRequest;
import facultate.android_backend.DTO.SignupRequest;
import facultate.android_backend.Domain.User;
import facultate.android_backend.Service.JwtService;
import facultate.android_backend.Service.UsersService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/users")
public class UsersController {
    private final UsersService usersService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    public UsersController(UsersService service,
                           JwtService jwtService,
                           AuthenticationManager authenticationManager,
                           PasswordEncoder passwordEncoder){
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
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
        if(isBlank(request.getUsername()) || isBlank(request.getPassword()) || isBlank(request.getEmail()))
            return ResponseEntity.badRequest().body(Map.of("message","Username, password and email are required"));
        if(!EMAIL.matcher(request.getEmail().trim()).matches())
            return ResponseEntity.badRequest().body(Map.of("message","Invalid email"));
        if(usersService.usernameTaken(request.getUsername()))
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message","Username already exists"));
        final User user = DtoTranslator.getFromDto(request);
        // stored hashed, otherwise the BCrypt login can never match it
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail().trim());
        user.setRoles("CUSTOMER");
        return usersService.signup(user)
                .map(u->ResponseEntity.ok(Map.of(
                        "message","User successfully created",
                        "id",u.getId()
                        ))
                ).orElseGet(()->ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("message","User wasn't created"))
                );
    }
    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    private static boolean isBlank(String value){
        return value == null || value.isBlank();
    }
}
