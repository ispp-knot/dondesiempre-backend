package ispp.project.dondesiempre.modules.clients.repositories;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

import ispp.project.dondesiempre.modules.clients.models.Client;

public interface ClientRepository extends JpaRepository<Client, UUID> {
  Optional<Client> findByUserId(UUID userId);
}
