package ispp.project.dondesiempre.modules.products.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductSizeCreationDTO {

  @NotBlank private String size;
}
