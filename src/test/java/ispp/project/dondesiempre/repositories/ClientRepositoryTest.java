package ispp.project.dondesiempre.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ispp.project.dondesiempre.models.Client;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class ClientRepositoryTest {

  @Autowired private ClientRepository clientRepository;

  @Test
  void testCreate() {
    Client client = new Client();
    Client saved = clientRepository.save(client);

    assertNotNull(saved.getId());
  }

  @Test
  void testRead() {
    Client client = new Client();
    Client saved = clientRepository.save(client);

    Client found = clientRepository.findById(saved.getId()).orElse(null);

    assertNotNull(found);
    assertEquals(saved.getId(), found.getId());
  }

  @Test
  void testUpdate() {
    Client client = new Client();
    Client saved = clientRepository.save(client);
    UUID originalId = saved.getId();

    Client updated = clientRepository.save(saved);

    assertEquals(originalId, updated.getId());
  }

  @Test
  void testDelete() {
    Client client = new Client();
    Client saved = clientRepository.save(client);
    UUID id = saved.getId();

    clientRepository.deleteById(id);

    assertTrue(clientRepository.findById(id).isEmpty());
  }
}
