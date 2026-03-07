package ispp.project.dondesiempre.modules.stores.models;

import ispp.project.dondesiempre.modules.common.models.BaseEntity;
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
@Table(name = "social_networks")
public class SocialNetwork extends BaseEntity {

  @Column
  @NotNull
  @Size(max = 255)
  String name;
}
