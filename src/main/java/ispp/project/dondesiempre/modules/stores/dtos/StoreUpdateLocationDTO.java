package ispp.project.dondesiempre.modules.stores.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StoreUpdateLocationDTO {

  @NotNull private Double latitude;

  @NotNull private Double longitude;
}
