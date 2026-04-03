package ispp.project.dondesiempre.modules.clients.services;

import ispp.project.dondesiempre.modules.auth.models.User;
import ispp.project.dondesiempre.modules.auth.services.UserService;
import ispp.project.dondesiempre.modules.clients.dtos.ClientDTO;
import ispp.project.dondesiempre.modules.clients.dtos.ClientUpdateDTO;
import ispp.project.dondesiempre.modules.clients.models.Client;
import ispp.project.dondesiempre.modules.clients.repositories.ClientRepository;
import ispp.project.dondesiempre.modules.common.exceptions.AlreadyExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ClientServiceTest {

    @Mock private ClientRepository clientRepository;
    @Mock private UserService userService;

    @InjectMocks private ClientService clientService;

    private User user;
    private User otherUser;
    private Client client;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("client@test.com");

        otherUser = new User();
        otherUser.setEmail("otherUser@test.com");
        otherUser.setId(UUID.randomUUID());

        client = new Client();
        client.setId(UUID.randomUUID());
        client.setName("client");
        client.setSurname("surname");
        client.setAddress("fake address");
        client.setPhone("1234567890");
        client.setEmail(user.getEmail());
        client.setUser(user);
    }

    @Test
    void shouldUpdateClientSameEmail() {
        when(userService.getCurrentClient()).thenReturn(client);
        when(userService.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(clientRepository.save(any(Client.class))).then(returnsFirstArg());

        ClientUpdateDTO dto = new ClientUpdateDTO();
        dto.setAddress("true address");
        dto.setPhone("0987654321");
        dto.setName("name");
        dto.setSurname("client surname");
        dto.setEmail(client.getEmail());

        ClientDTO response = clientService.updateClient(dto);

        assertEquals("true address", response.getAddress());
        assertEquals("0987654321", response.getPhone());
        assertEquals("name", response.getName());
        assertEquals("client surname", response.getSurname());
        assertEquals(user.getEmail(), response.getEmail());
        verify(userService).getCurrentClient();
        verify(userService).findByEmail(anyString());
        verify(userService).save(user);
        verify(clientRepository).save(client);
    }

    @Test
    void shouldUpdateClientNewEmail() {
        when(userService.getCurrentClient()).thenReturn(client);
        when(userService.findByEmail(anyString())).thenReturn(Optional.empty());
        when(clientRepository.save(any(Client.class))).then(returnsFirstArg());

        ClientUpdateDTO dto = new ClientUpdateDTO();
        dto.setAddress("true address");
        dto.setPhone(client.getPhone().get());
        dto.setName("name");
        dto.setSurname(client.getSurname());
        dto.setEmail("fake@test.com");

        ClientDTO response = clientService.updateClient(dto);

        assertEquals("true address", response.getAddress());
        assertEquals(client.getPhone().get(), response.getPhone());
        assertEquals("name", response.getName());
        assertEquals(client.getSurname(), response.getSurname());
        assertEquals("fake@test.com", response.getEmail());
        verify(userService).getCurrentClient();
        verify(userService).findByEmail(anyString());
        verify(userService).save(user);
        verify(clientRepository).save(client);
    }

    @Test
    void shouldNotUpdateClientRepeatedEmail() {
        when(userService.getCurrentClient()).thenReturn(client);
        when(userService.findByEmail(anyString())).thenReturn(Optional.of(otherUser));

        ClientUpdateDTO dto = new ClientUpdateDTO();
        dto.setAddress("true address");
        dto.setPhone(client.getPhone().get());
        dto.setName("name");
        dto.setSurname(client.getSurname());
        dto.setEmail(otherUser.getEmail());

        assertThrows(AlreadyExistsException.class, () -> clientService.updateClient(dto));

        verify(userService).getCurrentClient();
        verify(userService).findByEmail(anyString());
    }
}
