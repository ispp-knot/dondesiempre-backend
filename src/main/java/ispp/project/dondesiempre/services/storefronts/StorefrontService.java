package ispp.project.dondesiempre.services.storefronts;

import ispp.project.dondesiempre.exceptions.ResourceNotFoundException;
import ispp.project.dondesiempre.models.storefronts.Storefront;
import ispp.project.dondesiempre.models.storefronts.dto.StorefrontDTO;
import ispp.project.dondesiempre.repositories.storefronts.StorefrontRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StorefrontService {
  private final StorefrontRepository storefrontRepository;

  @Transactional(readOnly = true, rollbackFor = ResourceNotFoundException.class)
  public Storefront findById(UUID id) throws ResourceNotFoundException {
    return storefrontRepository
        .findById(id)
        .orElseThrow(
            () -> new ResourceNotFoundException("Storefront with ID " + id + " not found."));
  }

  @Transactional(readOnly = true, rollbackFor = ResourceNotFoundException.class)
  public StorefrontDTO getDTOById(UUID id) throws ResourceNotFoundException {
    return StorefrontDTO.fromStorefront(
        storefrontRepository
            .findById(id)
            .orElseThrow(
                () -> new ResourceNotFoundException("Storefront with ID " + id + " not found.")));
  }

  @Transactional(rollbackFor = ResourceNotFoundException.class)
  public StorefrontDTO updateStorefront(UUID id, StorefrontDTO dto) {

    Storefront storefront =
        storefrontRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Storefront" + id + "not found"));

    storefront.setIsFirstCollections(dto.getIsFirstCollections());
    storefront.setPrimaryColor(dto.getPrimaryColor());
    storefront.setSecondaryColor(dto.getSecondaryColor());
    storefront.setBannerImageUrl(dto.getBannerImageUrl());

    Storefront updatedStorefront = storefrontRepository.save(storefront);

    return StorefrontDTO.fromStorefront(updatedStorefront);
  }
}
