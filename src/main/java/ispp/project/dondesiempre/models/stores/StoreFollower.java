package ispp.project.dondesiempre.models.stores;

import ispp.project.dondesiempre.models.Client;
import ispp.project.dondesiempre.modules.common.models.BaseEntity;
import ispp.project.dondesiempre.modules.stores.models.Store;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@Setter
@Table(name = "store_followers")
public class StoreFollower extends BaseEntity {

  @NotNull
  @ManyToOne(optional = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  private Client client;

  @NotNull
  @ManyToOne(optional = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  private Store store;
}
