package ispp.project.dondesiempre.models.stores.dto;

import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StoreFollowerDTO {

  private UUID clientId;
  private UUID storeId;
  private Boolean isFollowing;

  public StoreFollowerDTO(UUID clientId, UUID storeId, Boolean isFollowing) {
    this.clientId = clientId;
    this.storeId = storeId;
    this.isFollowing = isFollowing;
  }
}
