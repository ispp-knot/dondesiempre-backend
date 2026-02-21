package ispp.project.dondesiempre.models.stores;

import ispp.project.dondesiempre.models.BaseEntity;
import ispp.project.dondesiempre.models.storefronts.Storefront;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "stores")
public class Store extends BaseEntity {

  @Column
  @NotNull
  @Size(max = 255)
  String name;

  @Column
  @NotNull
  @Size(max = 255)
  String location;

  @Column
  @NotNull
  @Size(max = 255)
  String openingHours;

  @Column(columnDefinition = "TEXT")
  @Size(max = 5000)
  String aboutUs;

  @OneToOne(mappedBy = "store")
  private Storefront storefront;
}
