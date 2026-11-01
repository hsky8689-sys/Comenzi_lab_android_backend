package facultate.android_backend.Domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.userdetails.UserDetails;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Getter
@Setter
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"username"})
},name="users"
)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String username;
    @Column
    private String password;
    @Column
    private String roles;
    @Column
    private String email;
    public User(Long id) {
        this.id=id;
    }
}
