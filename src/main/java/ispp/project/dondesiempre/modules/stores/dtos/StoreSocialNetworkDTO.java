package ispp.project.dondesiempre.modules.stores.dtos;

import ispp.project.dondesiempre.modules.stores.models.StoreSocialNetwork;
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
}
