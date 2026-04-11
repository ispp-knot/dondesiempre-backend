package ispp.project.dondesiempre.modules.stores.dtos;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

@Getter
@Setter
@NoArgsConstructor
public class StoreImageUpdateDTO {
  @NotNull
  @Min(0)
  @Max(4)
  private Integer displayOrder;

  @Nullable @URL private String image;
}
