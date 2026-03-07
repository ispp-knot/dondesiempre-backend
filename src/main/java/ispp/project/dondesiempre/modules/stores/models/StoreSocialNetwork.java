package ispp.project.dondesiempre.modules.stores.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import ispp.project.dondesiempre.modules.common.models.BaseEntity;

@Entity
@Getter
@Setter
@Table(name = "store_social_networks")
public class StoreSocialNetwork extends BaseEntity {

  @Column(length = 500)
  @NotNull
  @Size(max = 500)
  String link;

  @NotNull
  @ManyToOne(optional = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  private SocialNetwork socialNetwork;

  @NotNull
  @ManyToOne(optional = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  private Store store;
}
