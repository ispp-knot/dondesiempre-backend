package ispp.project.dondesiempre.modules.auth.repositories;

import ispp.project.dondesiempre.modules.auth.models.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, UUID> {
  Optional<User> findByEmail(String email);
}
