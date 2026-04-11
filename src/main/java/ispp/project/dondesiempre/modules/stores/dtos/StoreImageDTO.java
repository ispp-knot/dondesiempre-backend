package ispp.project.dondesiempre.modules.stores.dtos;

import ispp.project.dondesiempre.modules.stores.models.StoreImage;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

@Getter
@Setter
@NoArgsConstructor
public class StoreImageDTO {

  private UUID id;
  private Integer displayOrder;
  @URL @NotBlank private String image;

  public StoreImageDTO(StoreImage storeImage) {
    this.id = storeImage.getId();
    this.displayOrder = storeImage.getDisplayOrder();
    this.image = storeImage.getImage().orElse(null);
  }
}
