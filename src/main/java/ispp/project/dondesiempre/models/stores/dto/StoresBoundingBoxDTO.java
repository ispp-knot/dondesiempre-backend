package ispp.project.dondesiempre.models.stores.dto;

public interface StoresBoundingBoxDTO {
    Integer getId();
    String getName();
    String getEmail();
    String getStoreID();
    String getAddress();
    String getOpeningHours();
    String getPhone();
    Boolean getAcceptsShipping();
    Double getLatitude();
    Double getLongitude();
}
