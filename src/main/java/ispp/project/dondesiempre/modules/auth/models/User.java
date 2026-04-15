package ispp.project.dondesiempre.modules.auth.models;

import ispp.project.dondesiempre.modules.common.models.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Getter
@Setter
@Table(name = "users")
public class User extends BaseEntity {

  @Column(unique = true)
  @NotBlank
  @Email
  String email;

  @Column @NotBlank String password;

  @ColumnDefault("false")
  @Column(name = "terms_accepted", nullable = false)
  boolean termsAccepted = false;
}
