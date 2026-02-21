package ispp.project.dondesiempre.models.stores;

import ispp.project.dondesiempre.models.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

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

  @Column
  @Pattern(regexp = "^(\\+\\d{1,3}[- ]?)?\\d{7,15}$")
  String phone;

  @Column(columnDefinition = "TEXT")
  @Size(max = 5000)
  String aboutUs;

  @Column @NotNull Boolean acceptsShipping;
}
