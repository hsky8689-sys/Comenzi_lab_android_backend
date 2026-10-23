package facultate.android_backend.Domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.SoftDelete;
import org.hibernate.engine.internal.Cascade;

import java.util.ArrayList;
import java.util.List;

@Table(uniqueConstraints = {
   @UniqueConstraint(columnNames = {"name","adress","deleted"})
},name="restaurants")
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Getter
@Setter
@SoftDelete
public class Restaurant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String name;
    @Column
    private String adress;
    @OneToMany( cascade = CascadeType.MERGE, mappedBy = "restaurant")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private final List<Product> productList = new ArrayList<>();
    public Restaurant(long restaurant) {
        this.id = restaurant;
    }
}
