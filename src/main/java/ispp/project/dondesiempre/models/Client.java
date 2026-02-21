package ispp.project.dondesiempre.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "clients")
public class Client extends BaseEntity {

  @Column
  @NotBlank
  @Size(max = 255)
  String name;

  @Column
  @NotBlank
  @Size(max = 255)
  String surname;

  @Column @NotBlank @Email String email;

  @Column
  @Pattern(regexp = "^(\\+\\d{1,3}[- ]?)?\\d{7,15}$")
  String phone;

  @Column
  @Size(max = 255)
  String address;
}
