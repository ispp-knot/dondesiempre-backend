package ispp.project.dondesiempre.modules.stores.services;

import ispp.project.dondesiempre.modules.auth.services.AuthService;
import ispp.project.dondesiempre.modules.common.exceptions.ResourceNotFoundException;
import ispp.project.dondesiempre.modules.stores.dtos.StorefrontDTO;
import ispp.project.dondesiempre.modules.stores.models.Storefront;
import ispp.project.dondesiempre.modules.stores.repositories.StorefrontRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StorefrontService {
  private final StorefrontRepository storefrontRepository;
  private final ApplicationContext applicationContext;
  private final AuthService authService;

  @Transactional(readOnly = true, rollbackFor = ResourceNotFoundException.class)
  public Storefront findById(UUID id) throws ResourceNotFoundException {
    return storefrontRepository
        .findById(id)
        .orElseThrow(
            () -> new ResourceNotFoundException("Storefront with ID " + id + " not found."));
  }

  @Transactional(rollbackFor = ResourceNotFoundException.class)
  public StorefrontDTO updateStorefront(UUID id, StorefrontDTO dto)
      throws ResourceNotFoundException {
    Storefront storefront = applicationContext.getBean(StorefrontService.class).findById(id);
    authService.assertUserOwnsStore(storefront.getStore());

    storefront.setIsFirstCollections(dto.getIsFirstCollections());
    storefront.setPrimaryColor(dto.getPrimaryColor());
    storefront.setSecondaryColor(dto.getSecondaryColor());
    storefront.setBannerImageUrl(dto.getBannerImageUrl());

    Storefront updatedStorefront = storefrontRepository.save(storefront);

    return new StorefrontDTO(updatedStorefront);
  }
}
