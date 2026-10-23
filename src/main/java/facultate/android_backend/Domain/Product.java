package facultate.android_backend.Domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SoftDelete;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Builder
@SoftDelete
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"name","restaurant_id","deleted"})
},name="products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String name;
    @Column
    private long price;
    @OneToMany(mappedBy = "product",fetch = FetchType.LAZY)
    private List<ProductOrder> orderAppearences;
    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;
    public Product(long id) {
        this.id=id;
    }
}
