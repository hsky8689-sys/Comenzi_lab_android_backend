package facultate.android_backend.Service;

import facultate.android_backend.Config.Jwt.UserInfoDetails;
import facultate.android_backend.Domain.User;
import facultate.android_backend.Repository.UsersRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsersService implements UserDetailsService {
    private final UsersRepository usersRepository;
    public UsersService(UsersRepository repository){
        usersRepository = repository;
    }
    public Optional<User> login(String username,String password){
        return usersRepository.findByUsernameAndPassword(username,password);
    }
    @Transactional
    public Optional<User> signup(User entity){
        return Optional.of(usersRepository.save(entity));
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> found = usersRepository.findByUsername(username);
        return found.map(UserInfoDetails::new).orElseGet(() -> new UserInfoDetails(new User()));
    }
}
