package ispp.project.dondesiempre.modules.clients.dtos;

import ispp.project.dondesiempre.modules.common.validators.Phone;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ClientUpdateDTO {

  @NotBlank
  @Size(max = 255)
  private String name;

  @NotBlank
  @Size(max = 255)
  private String surname;

  @NotBlank @Email private String email;

  @Phone private String phone;

  @Size(max = 255)
  private String address;
}
