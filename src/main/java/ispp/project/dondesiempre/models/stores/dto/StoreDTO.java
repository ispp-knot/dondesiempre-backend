package ispp.project.dondesiempre.models.stores.dto;

import java.util.List;
import java.util.UUID;

import ispp.project.dondesiempre.models.storefronts.Storefront;
import ispp.project.dondesiempre.models.stores.Store;
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
  private String aboutUs;
  private Boolean acceptsShipping;
  private Double latitude;
  private Double longitude;
  private UUID storefrontId;
  private Boolean isFirstCollections;
  private String primaryColor;
  private String secondaryColor;
  private String bannerImageUrl;
  private List<SocialNetworkDTO> socialNetworks;

  public StoreDTO(Store store) {
    if (store == null) return;

    this.id = store.getId();
    this.name = store.getName();
    this.email = store.getEmail();
    this.storeID = store.getStoreID();
    this.address = store.getAddress();
    this.openingHours = store.getOpeningHours();
    this.phone = store.getPhone();
    this.aboutUs = store.getAboutUs();
    this.acceptsShipping = store.getAcceptsShipping();
    if (store.getLocation() != null) {
      this.latitude = store.getLocation().getY();
      this.longitude = store.getLocation().getX();
    }

    Storefront storefront = store.getStorefront();
    this.storefrontId = storefront.getId();
    this.isFirstCollections = storefront.getIsFirstCollections();
    this.primaryColor = storefront.getPrimaryColor();
    this.secondaryColor = storefront.getSecondaryColor();
    this.bannerImageUrl = storefront.getBannerImageUrl();
  }

  public StoreDTO() {}
}
