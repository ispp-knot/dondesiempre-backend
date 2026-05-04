package ispp.project.dondesiempre.modules.stores.dtos;

import ispp.project.dondesiempre.modules.stores.models.StoreSocialNetwork;
import ispp.project.dondesiempre.modules.stores.validators.ValidContactLink;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SocialNetworkDTO {
  private String name;

  @ValidContactLink
  @Size(max = 500)
  private String link;

  public SocialNetworkDTO(StoreSocialNetwork ssn) {
    // social media name
    this.name = ssn.getSocialNetwork().getName();
    this.link = ssn.getLink();
  }
}
