package ispp.project.dondesiempre.modules.auth.dtos;

import ispp.project.dondesiempre.modules.auth.validators.StrongPassword;
import ispp.project.dondesiempre.modules.common.validators.Phone;
import ispp.project.dondesiempre.modules.stores.validators.HexColor;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterStoreDTO {

  // User credentials
  @NotBlank
  @Email
  private String email;

  @NotBlank
  @StrongPassword
  private String password;

  // Store fields (required)
  @NotBlank
  @Size(max = 255)
  private String name;

  @NotBlank
  @Size(max = 255)
  private String storeID;

  @NotNull
  private Double latitude;

  @NotNull
  private Double longitude;

  @NotBlank
  @Size(max = 255)
  private String address;

  @NotBlank
  @Size(max = 255)
  private String openingHours;

  @NotNull
  private Boolean acceptsShipping;

  @NotBlank
  @Phone
  private String phone;

  @NotBlank
  @Size(max = 5000)
  private String aboutUs;

  // Storefront fields (required)
  @NotBlank
  @HexColor
  private String primaryColor;

  @NotBlank
  @HexColor
  private String secondaryColor;
}
