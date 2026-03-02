package ispp.project.dondesiempre.models;

import ispp.project.dondesiempre.validators.Phone;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

  @Column @Phone String phone;

  @Column
  @Size(max = 255)
  String address;

  @NotNull
  @OneToOne(optional = false)
  @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
  private User user;
}
