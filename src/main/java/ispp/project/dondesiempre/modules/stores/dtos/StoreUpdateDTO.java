package ispp.project.dondesiempre.modules.stores.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StoreUpdateDTO {
  private String name;
  private String email;
  private String storeID;
  private String address;
  private String openingHours;
  private String phone;
  private String aboutUs;
  // TODO social network

}
