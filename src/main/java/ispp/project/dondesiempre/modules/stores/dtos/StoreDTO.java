package ispp.project.dondesiempre.modules.stores.dtos;

import ispp.project.dondesiempre.modules.stores.models.Store;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StoreDTO {

  private UUID id;
  private String name;
  private String email;
  private String address;
  private String openingHours;
  private Boolean hasActivePromotions;
  private Double latitude;
  private Double longitude;
  private String aboutUs;
  private StorefrontDTO storefront;
  private List<StoreSocialNetworkDTO> socialNetworks;

  public StoreDTO(Store store) {
    if (store == null) return;

    this.id = store.getId();
    this.name = store.getName();
    this.email = store.getEmail();
    this.address = store.getAddress();
    this.openingHours = store.getOpeningHours();
    this.aboutUs = store.getAboutUs().orElse(null);
    if (store.getLocation() != null) {
      this.latitude = store.getLocation().getY();
      this.longitude = store.getLocation().getX();
    }
    this.storefront = new StorefrontDTO(store.getStorefront());
  }
}
