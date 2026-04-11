package ispp.project.dondesiempre.modules.stores.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StoreUpdateDTO {

  @Size(max = 255)
  private String name;

  @Email private String email;

  @Size(max = 255)
  private String address;

  @Size(max = 255)
  private String openingHours;

  @Size(max = 5000)
  private String aboutUs;
  // TODO social network

}
