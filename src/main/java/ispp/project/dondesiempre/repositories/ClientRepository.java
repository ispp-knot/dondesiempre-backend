package ispp.project.dondesiempre.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import ispp.project.dondesiempre.models.Client;

public interface ClientRepository extends JpaRepository<Client, UUID> {

}
