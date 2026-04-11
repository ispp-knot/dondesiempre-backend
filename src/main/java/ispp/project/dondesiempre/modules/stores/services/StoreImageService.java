package ispp.project.dondesiempre.modules.stores.services;
import ispp.project.dondesiempre.modules.auth.services.AuthService;
import ispp.project.dondesiempre.modules.common.exceptions.InvalidBoundingBoxException;
import ispp.project.dondesiempre.modules.common.exceptions.InvalidRequestException;
import ispp.project.dondesiempre.modules.common.exceptions.ResourceNotFoundException;
import ispp.project.dondesiempre.modules.common.exceptions.UnauthorizedException;
import ispp.project.dondesiempre.modules.promotions.repositories.PromotionRepository;
import ispp.project.dondesiempre.modules.stores.dtos.StoreDTO;
import ispp.project.dondesiempre.modules.stores.dtos.StoreImageDTO;
import ispp.project.dondesiempre.modules.stores.dtos.StoreImageUpdateDTO;
import ispp.project.dondesiempre.modules.stores.dtos.StoreSocialNetworkDTO;
import ispp.project.dondesiempre.modules.stores.dtos.StoreUpdateDTO;
import ispp.project.dondesiempre.modules.stores.models.Store;
import ispp.project.dondesiempre.modules.stores.models.StoreImage;
import ispp.project.dondesiempre.modules.stores.models.StoreSocialNetwork;
import ispp.project.dondesiempre.modules.stores.repositories.StoreImageRepository;
import ispp.project.dondesiempre.modules.stores.repositories.StoreRepository;
import ispp.project.dondesiempre.modules.stores.repositories.StoreSocialNetworkRepository;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StoreImageService {
    private final StoreRepository storeRepository;
    private final StoreImageRepository storeImageRepository;
    private final ApplicationContext applicationContext;
    private final AuthService authService;

  @Transactional(readOnly = true, rollbackFor = ResourceNotFoundException.class)
  public Store findById(UUID id) throws ResourceNotFoundException {
    return storeRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Store with ID " + id + " not found."));
  }
  @Transactional(readOnly = true, rollbackFor = ResourceNotFoundException.class)
  public StoreImage findImageById(UUID id) throws ResourceNotFoundException {
    return storeImageRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Image with ID " + id + " not found."));
  }
  @Transactional(readOnly = true)
  public List<StoreImage> findImageByStoreId(UUID storeId) {
    return storeImageRepository.findImagesByStoreIdOrderByDisplayOrder(storeId);
  }
  @Transactional(readOnly = true)
  public StoreImageDTO toDTO(StoreImage storeImage) {
    return new StoreImageDTO(storeImage);
  }
  public StoreImageDTO add(UUID id, StoreImageUpdateDTO dto)
      throws UnauthorizedException, ResourceNotFoundException, InvalidRequestException {

    Store storeToUpdate = applicationContext.getBean(StoreService.class).findById(id);
    authService.assertUserOwnsStore(storeToUpdate);

    List<StoreImage> existingImages =
        storeImageRepository.findImagesByStoreIdOrderByDisplayOrder(id);

    if (existingImages.size() >= 5) {
      throw new InvalidRequestException("A store cannot have more than 5 images.");
    }

    StoreImage image = new StoreImage();
    image.setImage(dto.getImage());
    image.setDisplayOrder(dto.getDisplayOrder());
    image.setStore(storeToUpdate);

    return toDTO(storeImageRepository.save(image));
  }

    @Transactional(rollbackFor = {UnauthorizedException.class, ResourceNotFoundException.class})
    public StoreImageDTO update(UUID imageId, StoreImageUpdateDTO dto)
        throws UnauthorizedException, ResourceNotFoundException {

    StoreImage imageToUpdate = findImageById(imageId);
    authService.assertUserOwnsStore(imageToUpdate.getStore());

    imageToUpdate.setImage(dto.getImage());
    imageToUpdate.setDisplayOrder(dto.getDisplayOrder());

    return toDTO(storeImageRepository.save(imageToUpdate));
    }

  @Transactional(rollbackFor = {UnauthorizedException.class, ResourceNotFoundException.class})
  public void delete(UUID id) throws UnauthorizedException, ResourceNotFoundException {
    StoreImage imageToDelete = findImageById(id);
    authService.assertUserOwnsStore(imageToDelete.getStore());
    storeImageRepository.delete(imageToDelete);
  }
}
