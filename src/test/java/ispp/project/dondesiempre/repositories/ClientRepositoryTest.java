package ispp.project.dondesiempre.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ispp.project.dondesiempre.models.Client;
import ispp.project.dondesiempre.models.User;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class ClientRepositoryTest {

  @Autowired private ClientRepository clientRepository;
  @Autowired private UserRepository userRepository;

  private static final AtomicInteger userCounter = new AtomicInteger(0);

  private Client newClientWithUser() {
    User user = new User();
    user.setEmail("client-test-" + userCounter.incrementAndGet() + "@test.com");
    user.setPassword("password");
    userRepository.save(user);

    Client client = new Client();
    client.setUser(user);
    return client;
  }

  @Test
  void testCreate() {
    Client saved = clientRepository.save(newClientWithUser());
    assertNotNull(saved.getId());
  }

  @Test
  void testRead() {
    Client saved = clientRepository.save(newClientWithUser());
    Client found = clientRepository.findById(saved.getId()).orElse(null);
    assertNotNull(found);
    assertEquals(saved.getId(), found.getId());
  }

  @Test
  void testUpdate() {
    Client saved = clientRepository.save(newClientWithUser());
    UUID originalId = saved.getId();
    Client updated = clientRepository.save(saved);
    assertEquals(originalId, updated.getId());
  }

  @Test
  void testDelete() {
    Client saved = clientRepository.save(newClientWithUser());
    UUID id = saved.getId();
    clientRepository.deleteById(id);
    assertTrue(clientRepository.findById(id).isEmpty());
  }
}
