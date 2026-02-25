package ispp.project.dondesiempre.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryCreationDTO {

  @NotBlank
  @Size(max = 255)
  private String name;

  @Size(max = 1000)
  private String description;
}
