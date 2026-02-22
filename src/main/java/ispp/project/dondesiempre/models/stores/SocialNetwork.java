package ispp.project.dondesiempre.models.stores;

import ispp.project.dondesiempre.models.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "socialNetworks")
public class SocialNetwork extends BaseEntity {

  @Column
  @NotNull
  @Size(max = 255)
  String name;
}
