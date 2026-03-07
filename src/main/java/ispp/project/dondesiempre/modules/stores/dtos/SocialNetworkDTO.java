package ispp.project.dondesiempre.modules.stores.dtos;

import ispp.project.dondesiempre.modules.stores.models.StoreSocialNetwork;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SocialNetworkDTO {
  private String name;
  private String link;

  public SocialNetworkDTO(StoreSocialNetwork ssn) {
    this.name = ssn.getSocialNetwork().getName();
    this.link = ssn.getLink();
  }
}
