package ispp.project.dondesiempre.controllers.auth.dto;

import ispp.project.dondesiempre.models.clients.ClientDTO;
import ispp.project.dondesiempre.models.stores.dto.StoreDTO;
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
