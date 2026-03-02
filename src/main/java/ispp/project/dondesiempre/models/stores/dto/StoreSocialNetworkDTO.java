package ispp.project.dondesiempre.models.stores.dto;

import ispp.project.dondesiempre.models.stores.StoreSocialNetwork;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StoreSocialNetworkDTO {

  private String name;
  private String link;

  public StoreSocialNetworkDTO(StoreSocialNetwork ssn) {
    this.name = ssn.getSocialNetwork().getName();
    this.link = ssn.getLink();
  }

  public StoreSocialNetworkDTO() {}
}
