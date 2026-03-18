package ispp.project.dondesiempre.modules.stores.services;

import ispp.project.dondesiempre.modules.auth.services.AuthService;
import ispp.project.dondesiempre.modules.common.exceptions.ResourceNotFoundException;
import ispp.project.dondesiempre.modules.common.exceptions.UnauthorizedException;
import ispp.project.dondesiempre.modules.stores.dtos.StorefrontDTO;
import ispp.project.dondesiempre.modules.stores.models.Storefront;
import ispp.project.dondesiempre.modules.stores.repositories.StorefrontRepository;
import ispp.project.dondesiempre.utils.cloudinary.CloudinaryService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class StorefrontService {
  private final StorefrontRepository storefrontRepository;
  private final ApplicationContext applicationContext;
  private final AuthService authService;
  private final CloudinaryService cloudinaryService;

  @Transactional(readOnly = true, rollbackFor = ResourceNotFoundException.class)
  public Storefront findById(UUID id) throws ResourceNotFoundException {
    return storefrontRepository
        .findById(id)
        .orElseThrow(
            () -> new ResourceNotFoundException("Storefront with ID " + id + " not found."));
  }

  @Transactional(rollbackFor = {UnauthorizedException.class, ResourceNotFoundException.class})
  public StorefrontDTO updateStorefront(UUID id, StorefrontDTO dto, MultipartFile image)
      throws UnauthorizedException, ResourceNotFoundException {
    Storefront storefront = applicationContext.getBean(StorefrontService.class).findById(id);
    authService.assertUserOwnsStore(storefront.getStore());

    if (dto.getIsFirstCollections() != null)
      storefront.setIsFirstCollections(dto.getIsFirstCollections());

    if (dto.getPrimaryColor() != null) storefront.setPrimaryColor(dto.getPrimaryColor());

    if (dto.getSecondaryColor() != null) storefront.setSecondaryColor(dto.getSecondaryColor());

    if (image != null) storefront.setBannerImageUrl(cloudinaryService.upload(image));

    Storefront updatedStorefront = storefrontRepository.save(storefront);

    return new StorefrontDTO(updatedStorefront);
  }
}
