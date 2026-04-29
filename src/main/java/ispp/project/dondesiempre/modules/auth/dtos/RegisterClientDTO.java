package ispp.project.dondesiempre.modules.auth.dtos;

import ispp.project.dondesiempre.modules.auth.validators.StrongPassword;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterClientDTO {

  // User credentials
  @NotBlank @Email private String email;

  @NotBlank @StrongPassword private String password;

  // Client fields (required)
  @NotBlank
  @Size(max = 255)
  private String name;

  @NotBlank
  @Size(max = 255)
  private String surname;

  @NotNull
  @AssertTrue(message = "Debe aceptar los términos de servicio")
  private Boolean termsAccepted;
}
