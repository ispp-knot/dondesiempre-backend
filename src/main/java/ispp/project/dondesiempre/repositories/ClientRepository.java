package ispp.project.dondesiempre.repositories;

import ispp.project.dondesiempre.models.clients.Client;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, UUID> {
  Optional<Client> findByUserId(UUID userId);
}
