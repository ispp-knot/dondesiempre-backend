package ispp.project.dondesiempre.modules.auth.dtos;

import ispp.project.dondesiempre.modules.auth.validators.StrongPassword;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordDTO {

  @NotBlank private String oldPassword;

  @NotBlank @StrongPassword private String newPassword;
}
