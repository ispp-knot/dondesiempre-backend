package ispp.project.dondesiempre.repositories.stores;

import java.util.UUID;

import ispp.project.dondesiempre.models.stores.SocialNetwork;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SocialNetworkRepository extends JpaRepository<SocialNetwork, UUID> {}
