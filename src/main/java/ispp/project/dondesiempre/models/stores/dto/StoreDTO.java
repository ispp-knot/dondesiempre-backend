package ispp.project.dondesiempre.models.stores.dto;

import ispp.project.dondesiempre.models.stores.Store;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StoreDTO {

  private UUID id;
  private String name;
  private String email;
  private String storeID;
  private String address;
  private String openingHours;
  private String phone;
  private Boolean acceptsShipping;
  private Double latitude;
  private Double longitude;

  public StoreDTO(Store store) {
    if (store == null) return;

    this.id = store.getId();
    this.name = store.getName();
    this.email = store.getEmail();
    this.storeID = store.getStoreID();
    this.address = store.getAddress();
    this.openingHours = store.getOpeningHours();
    this.phone = store.getPhone();
    this.acceptsShipping = store.getAcceptsShipping();
    if (store.getLocation() != null) {
      this.latitude = store.getLocation().getY();
      this.longitude = store.getLocation().getX();
    }
  }

  public StoreDTO() {}
}
