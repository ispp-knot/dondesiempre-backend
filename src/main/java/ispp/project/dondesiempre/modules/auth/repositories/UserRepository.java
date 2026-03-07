package ispp.project.dondesiempre.modules.auth.repositories;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

import ispp.project.dondesiempre.modules.auth.models.User;

public interface UserRepository extends JpaRepository<User, UUID> {
  Optional<User> findByEmail(String email);
}
