package ispp.project.dondesiempre.modules.clients.models;

import ispp.project.dondesiempre.modules.auth.models.User;
import ispp.project.dondesiempre.modules.common.models.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
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

  @NotNull
  @OneToOne(optional = false)
  @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
  private User user;
}
