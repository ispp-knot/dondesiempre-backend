package ispp.project.dondesiempre.modules.stores.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ispp.project.dondesiempre.modules.auth.models.User;
import ispp.project.dondesiempre.modules.auth.services.AuthService;
import ispp.project.dondesiempre.modules.common.exceptions.InvalidRequestException;
import ispp.project.dondesiempre.modules.common.exceptions.ResourceNotFoundException;
import ispp.project.dondesiempre.modules.common.exceptions.UnauthorizedException;
import ispp.project.dondesiempre.modules.stores.dtos.StoreImageDTO;
import ispp.project.dondesiempre.modules.stores.dtos.StoreImageUpdateDTO;
import ispp.project.dondesiempre.modules.stores.models.Store;
import ispp.project.dondesiempre.modules.stores.models.StoreImage;
import ispp.project.dondesiempre.modules.stores.repositories.StoreImageRepository;
import ispp.project.dondesiempre.modules.stores.repositories.StoreRepository;
import ispp.project.dondesiempre.utils.cloudinary.CloudinaryService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class StoreImageServiceTest {

  @Mock private StoreRepository storeRepository;
  @Mock private StoreImageRepository storeImageRepository;
  @Mock private ApplicationContext applicationContext;
  @Mock private AuthService authService;
  @Mock private StoreService storeService;
  @Mock private CloudinaryService cloudinaryService;

  @InjectMocks private StoreImageService storeImageService;

  private Store store;
  private StoreImage storeImage;
  private UUID storeId;
  private UUID imageId;
  private MockMultipartFile imageFile;

  private StoreImage createStoreImageWithOrder(int order) {
    StoreImage image = new StoreImage();
    image.setId(UUID.randomUUID());
    image.setStore(store);
    image.setImage("https://example.com/image-" + order + ".jpg");
    image.setDisplayOrder(order);
    return image;
  }

  @BeforeEach
  void setUp() {
    storeId = UUID.randomUUID();
    imageId = UUID.randomUUID();

    store = new Store();
    store.setId(storeId);
    store.setName("Tienda de prueba");

    User owner = new User();
    owner.setEmail("owner@test.com");
    store.setUser(owner);

    storeImage = new StoreImage();
    storeImage.setId(imageId);
    storeImage.setStore(store);
    storeImage.setImage("https://example.com/image.jpg");
    storeImage.setDisplayOrder(0);

    imageFile =
        new MockMultipartFile("image", "test.jpg", "image/jpeg", "test image content".getBytes());
  }

  @Test
  void shouldReturnStore_whenFindByIdExists() {
    when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));

    Store result = storeImageService.findById(storeId);

    assertEquals(storeId, result.getId());
    assertEquals("Tienda de prueba", result.getName());
    verify(storeRepository, times(1)).findById(storeId);
  }

  @Test
  void shouldThrowResourceNotFoundException_whenFindByIdDoesNotExist() {
    UUID nonExistentId = UUID.randomUUID();
    when(storeRepository.findById(nonExistentId)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> storeImageService.findById(nonExistentId));

    verify(storeRepository, times(1)).findById(nonExistentId);
  }

  @Test
  void shouldReturnImage_whenFindImageByIdExists() {
    when(storeImageRepository.findById(imageId)).thenReturn(Optional.of(storeImage));

    StoreImage result = storeImageService.findImageById(imageId);

    assertEquals(imageId, result.getId());
    assertEquals(0, result.getDisplayOrder());
    assertEquals("https://example.com/image.jpg", result.getImage().orElse(null));
    verify(storeImageRepository, times(1)).findById(imageId);
  }

  @Test
  void shouldThrowResourceNotFoundException_whenFindImageByIdDoesNotExist() {
    UUID nonExistentId = UUID.randomUUID();
    when(storeImageRepository.findById(nonExistentId)).thenReturn(Optional.empty());

    assertThrows(
        ResourceNotFoundException.class, () -> storeImageService.findImageById(nonExistentId));

    verify(storeImageRepository, times(1)).findById(nonExistentId);
  }

  @Test
  void shouldReturnImagesByStoreIdOrdered() {
    StoreImage secondImage = new StoreImage();
    secondImage.setId(UUID.randomUUID());
    secondImage.setStore(store);
    secondImage.setImage("https://example.com/image2.jpg");
    secondImage.setDisplayOrder(1);

    when(storeImageRepository.findImagesByStoreIdOrderByDisplayOrder(storeId))
        .thenReturn(List.of(storeImage, secondImage));

    List<StoreImage> result = storeImageService.findImageByStoreId(storeId);

    assertEquals(2, result.size());
    assertEquals(0, result.get(0).getDisplayOrder());
    assertEquals(1, result.get(1).getDisplayOrder());
    verify(storeImageRepository, times(1)).findImagesByStoreIdOrderByDisplayOrder(storeId);
  }

  @Test
  void shouldConvertStoreImageToDTO() {
    StoreImageDTO result = storeImageService.toDTO(storeImage);

    assertNotNull(result);
    assertEquals(storeImage.getId(), result.getId());
    assertEquals(storeImage.getDisplayOrder(), result.getDisplayOrder());
    assertEquals(storeImage.getImage().orElse(null), result.getImage());
  }

  @Test
  void shouldAddImageSuccessfully()
      throws UnauthorizedException, ResourceNotFoundException, InvalidRequestException {
    StoreImageUpdateDTO dto = new StoreImageUpdateDTO();
    dto.setDisplayOrder(2);

    StoreImage savedImage = new StoreImage();
    savedImage.setId(UUID.randomUUID());
    savedImage.setStore(store);
    savedImage.setImage("https://example.com/new-image-from-cloudinary.jpg");
    savedImage.setDisplayOrder(dto.getDisplayOrder());

    when(applicationContext.getBean(StoreService.class)).thenReturn(storeService);
    when(storeService.findById(storeId)).thenReturn(store);
    when(cloudinaryService.upload(any(MultipartFile.class)))
        .thenReturn("https://example.com/new-image-from-cloudinary.jpg");
    when(storeImageRepository.save(any(StoreImage.class))).thenReturn(savedImage);

    StoreImageDTO result = storeImageService.add(storeId, dto, imageFile);

    assertNotNull(result);
    assertEquals("https://example.com/new-image-from-cloudinary.jpg", result.getImage());
    assertEquals(2, result.getDisplayOrder());

    verify(applicationContext, times(1)).getBean(StoreService.class);
    verify(storeService, times(1)).findById(storeId);
    verify(authService, times(1)).assertUserOwnsStore(store);
    verify(cloudinaryService, times(1)).upload(any(MultipartFile.class));
    verify(storeImageRepository, times(1)).save(any(StoreImage.class));
  }

  @Test
  void shouldThrowResourceNotFoundException_whenAddingImageToNonExistentStore()
      throws ResourceNotFoundException {
    StoreImageUpdateDTO dto = new StoreImageUpdateDTO();
    dto.setDisplayOrder(0);

    when(applicationContext.getBean(StoreService.class)).thenReturn(storeService);
    when(storeService.findById(storeId))
        .thenThrow(new ResourceNotFoundException("Store not found"));

    assertThrows(
        ResourceNotFoundException.class, () -> storeImageService.add(storeId, dto, imageFile));

    verify(authService, never()).assertUserOwnsStore(any());
    verify(storeImageRepository, never()).save(any());
  }

  @Test
  void shouldThrowUnauthorizedException_whenAddingImageWithoutOwnership()
      throws UnauthorizedException, ResourceNotFoundException {
    StoreImageUpdateDTO dto = new StoreImageUpdateDTO();
    dto.setDisplayOrder(0);

    when(applicationContext.getBean(StoreService.class)).thenReturn(storeService);
    when(storeService.findById(storeId)).thenReturn(store);
    doThrow(new UnauthorizedException("Not owner")).when(authService).assertUserOwnsStore(store);

    assertThrows(UnauthorizedException.class, () -> storeImageService.add(storeId, dto, imageFile));

    verify(storeImageRepository, never()).save(any());
  }

  @Test
  void shouldThrowInvalidRequestException_whenAddingMoreThanFiveImages()
      throws UnauthorizedException, ResourceNotFoundException {

    StoreImageUpdateDTO dto = new StoreImageUpdateDTO();
    dto.setDisplayOrder(4);

    List<StoreImage> existingImages =
        List.of(
            createStoreImageWithOrder(0),
            createStoreImageWithOrder(1),
            createStoreImageWithOrder(2),
            createStoreImageWithOrder(3),
            createStoreImageWithOrder(4));

    when(applicationContext.getBean(StoreService.class)).thenReturn(storeService);
    when(storeService.findById(storeId)).thenReturn(store);
    when(storeImageRepository.findImagesByStoreIdOrderByDisplayOrder(storeId))
        .thenReturn(existingImages);

    assertThrows(
        InvalidRequestException.class, () -> storeImageService.add(storeId, dto, imageFile));

    verify(authService, times(1)).assertUserOwnsStore(store);
    verify(storeImageRepository, never()).save(any());
  }

  @Test
  void shouldThrowInvalidRequestException_whenAddingImageWithoutFile()
      throws UnauthorizedException, ResourceNotFoundException {

    StoreImageUpdateDTO dto = new StoreImageUpdateDTO();
    dto.setDisplayOrder(0);

    when(applicationContext.getBean(StoreService.class)).thenReturn(storeService);
    when(storeService.findById(storeId)).thenReturn(store);

    assertThrows(InvalidRequestException.class, () -> storeImageService.add(storeId, dto, null));

    verify(authService, times(1)).assertUserOwnsStore(store);
    verify(storeImageRepository, never()).save(any());
  }

  @Test
  void shouldUpdateImageSuccessfully() throws UnauthorizedException, ResourceNotFoundException {
    StoreImageUpdateDTO dto = new StoreImageUpdateDTO();
    dto.setImage("https://example.com/updated-image.jpg");
    dto.setDisplayOrder(3);

    when(storeImageRepository.findById(imageId)).thenReturn(Optional.of(storeImage));
    when(storeImageRepository.save(storeImage)).thenReturn(storeImage);

    StoreImageDTO result = storeImageService.update(imageId, dto);

    assertNotNull(result);
    assertEquals("https://example.com/updated-image.jpg", result.getImage());
    assertEquals(3, result.getDisplayOrder());
    assertEquals("https://example.com/updated-image.jpg", storeImage.getImage().orElse(null));
    assertEquals(3, storeImage.getDisplayOrder());

    verify(storeImageRepository, times(1)).findById(imageId);
    verify(authService, times(1)).assertUserOwnsStore(store);
    verify(storeImageRepository, times(1)).save(storeImage);
  }

  @Test
  void shouldThrowResourceNotFoundException_whenUpdatingNonExistentImage() {
    StoreImageUpdateDTO dto = new StoreImageUpdateDTO();
    dto.setImage("https://example.com/updated-image.jpg");
    dto.setDisplayOrder(1);

    UUID nonExistentId = UUID.randomUUID();
    when(storeImageRepository.findById(nonExistentId)).thenReturn(Optional.empty());

    assertThrows(
        ResourceNotFoundException.class, () -> storeImageService.update(nonExistentId, dto));

    verify(authService, never()).assertUserOwnsStore(any());
    verify(storeImageRepository, never()).save(any());
  }

  @Test
  void shouldThrowUnauthorizedException_whenUpdatingImageWithoutOwnership()
      throws UnauthorizedException {
    StoreImageUpdateDTO dto = new StoreImageUpdateDTO();
    dto.setImage("https://example.com/updated-image.jpg");
    dto.setDisplayOrder(1);

    when(storeImageRepository.findById(imageId)).thenReturn(Optional.of(storeImage));
    doThrow(new UnauthorizedException("Not owner")).when(authService).assertUserOwnsStore(store);

    assertThrows(UnauthorizedException.class, () -> storeImageService.update(imageId, dto));

    verify(storeImageRepository, never()).save(any());
  }

  @Test
  void shouldDeleteImageSuccessfully() throws UnauthorizedException, ResourceNotFoundException {
    when(storeImageRepository.findById(imageId)).thenReturn(Optional.of(storeImage));

    storeImageService.delete(imageId);

    verify(storeImageRepository, times(1)).findById(imageId);
    verify(authService, times(1)).assertUserOwnsStore(store);
    verify(storeImageRepository, times(1)).delete(storeImage);
  }

  @Test
  void shouldThrowResourceNotFoundException_whenDeletingNonExistentImage() {
    UUID nonExistentId = UUID.randomUUID();
    when(storeImageRepository.findById(nonExistentId)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> storeImageService.delete(nonExistentId));

    verify(authService, never()).assertUserOwnsStore(any());
    verify(storeImageRepository, never()).delete(any());
  }

  @Test
  void shouldThrowUnauthorizedException_whenDeletingImageWithoutOwnership()
      throws UnauthorizedException {
    when(storeImageRepository.findById(imageId)).thenReturn(Optional.of(storeImage));
    doThrow(new UnauthorizedException("Not owner")).when(authService).assertUserOwnsStore(store);

    assertThrows(UnauthorizedException.class, () -> storeImageService.delete(imageId));

    verify(storeImageRepository, never()).delete(any());
  }
}
