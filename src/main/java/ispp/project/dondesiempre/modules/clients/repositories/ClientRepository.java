package ispp.project.dondesiempre.modules.clients.repositories;

import ispp.project.dondesiempre.modules.clients.models.Client;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, UUID> {
  Optional<Client> findByUserId(UUID userId);
}
