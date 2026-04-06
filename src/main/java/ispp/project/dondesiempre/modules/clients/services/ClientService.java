package ispp.project.dondesiempre.modules.clients.services;

import ispp.project.dondesiempre.modules.auth.models.User;
import ispp.project.dondesiempre.modules.auth.services.UserService;
import ispp.project.dondesiempre.modules.clients.dtos.ClientDTO;
import ispp.project.dondesiempre.modules.clients.dtos.ClientUpdateDTO;
import ispp.project.dondesiempre.modules.clients.models.Client;
import ispp.project.dondesiempre.modules.clients.repositories.ClientRepository;
import ispp.project.dondesiempre.modules.common.exceptions.AlreadyExistsException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClientService {
  private final ClientRepository clientRepository;
  private final UserService userService;

  @Transactional
  public ClientDTO updateClient(ClientUpdateDTO dto) {
    Client currentClient = userService.getCurrentClient();
    User currentUser = currentClient.getUser();

    Optional<User> userByEmail = userService.findByEmail(dto.getEmail());
    if (userByEmail.isPresent() && !userByEmail.get().equals(currentUser)) {
      throw new AlreadyExistsException("Email already in use.");
    }

    currentUser.setEmail(dto.getEmail());
    userService.save(currentUser);

    currentClient.setName(dto.getName());
    currentClient.setSurname(dto.getSurname());
    currentClient.setEmail(dto.getEmail());
    currentClient.setAddress(dto.getAddress());
    currentClient.setPhone(dto.getPhone());
    return new ClientDTO(clientRepository.save(currentClient));
  }
}
