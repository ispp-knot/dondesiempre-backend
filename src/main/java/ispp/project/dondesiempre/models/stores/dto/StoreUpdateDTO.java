package ispp.project.dondesiempre.models.stores.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StoreUpdateDTO {
  private String name;
  private String email;
  private String storeID; // No sé si esto se puede actualizar, pero lo dejo por si acaso
  private String address;
  private String openingHours;
  private String phone;
  private String aboutUs;
  // Faltan las redes sociales, pero no sé cómo las vamos a actualizar, habría que crear otro DTO de
  // social network creo yo

}
