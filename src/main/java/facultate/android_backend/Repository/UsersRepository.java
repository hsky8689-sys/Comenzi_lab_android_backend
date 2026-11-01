package facultate.android_backend.Repository;

import facultate.android_backend.Domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<User,Long> {
    Optional<User> findByUsernameAndPassword(String username,String password);
    Optional<User> findByUsername(String username);
    User getUserByUsername(String username);
    @Query(value = "SELECT u.id from User u where u.username = :username")
    Optional<Long> findIdByUsername(String username);
}
