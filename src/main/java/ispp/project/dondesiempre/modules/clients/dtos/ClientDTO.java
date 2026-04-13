package ispp.project.dondesiempre.modules.clients.dtos;

import ispp.project.dondesiempre.modules.clients.models.Client;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ClientDTO {

  private UUID id;
  private String name;
  private String surname;
  private String email;

  public ClientDTO(Client client) {
    this.id = client.getId();
    this.name = client.getName();
    this.surname = client.getSurname();
    this.email = client.getUser().getEmail();
  }
}
