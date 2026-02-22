package ispp.project.dondesiempre.models.stores;

import ispp.project.dondesiempre.models.BaseEntity;
import ispp.project.dondesiempre.models.products.Product;
import ispp.project.dondesiempre.validators.Phone;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@Setter
@Table(name = "stores")
public class Store extends BaseEntity {

  @Column
  @NotBlank
  @Size(max = 255)
  String name;

  @Column @NotBlank @Email String email;

  @Column
  @NotBlank
  @Size(max = 255)
  String storeID;

  @Column
  @NotBlank
  @Size(max = 255)
  String location;

  @Column
  @NotBlank
  @Size(max = 255)
  String openingHours;

  @Column @Phone String phone;

  @Column(columnDefinition = "TEXT")
  @Size(max = 5000)
  String aboutUs;

  @Column @NotNull Boolean acceptsShipping;

  @NotNull
  @OneToMany
  @JoinColumn(name = "store_id")
  @OnDelete(action = OnDeleteAction.CASCADE)
  private List<Product> products;
}
