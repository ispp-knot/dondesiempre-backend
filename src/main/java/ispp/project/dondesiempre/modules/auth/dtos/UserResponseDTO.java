package ispp.project.dondesiempre.modules.auth.dtos;

import ispp.project.dondesiempre.modules.clients.dtos.ClientDTO;
import ispp.project.dondesiempre.modules.stores.dtos.StoreDTO;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record UserResponseDTO(
    UUID id,
    String email,
    List<String> roles,
    Instant expiresAt,
    StoreDTO store,
    ClientDTO client) {}
