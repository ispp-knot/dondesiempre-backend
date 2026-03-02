package ispp.project.dondesiempre.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ispp.project.dondesiempre.exceptions.ResourceNotFoundException;
import ispp.project.dondesiempre.models.storefronts.Storefront;
import ispp.project.dondesiempre.models.storefronts.dto.StorefrontDTO;
import ispp.project.dondesiempre.repositories.storefronts.StorefrontRepository;
import ispp.project.dondesiempre.services.storefronts.StorefrontService;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class StorefrontServiceTest {

  @Mock private StorefrontRepository storefrontRepository;

  @InjectMocks private StorefrontService storefrontService;

  private Storefront storefront;
  private UUID storefrontId;

  @BeforeEach
  void setUp() {
    storefrontId = UUID.randomUUID();
    storefront = new Storefront();
    storefront.setId(storefrontId);
    storefront.setIsFirstCollections(true);
    storefront.setPrimaryColor("#c65a3a");
    storefront.setSecondaryColor("#19756a");
    storefront.setBannerImageUrl("http://example.com/banner.png");
  }

  @Test
  void shouldReturnStorefront_whenFindByIdExists() {
    when(storefrontRepository.findById(storefrontId)).thenReturn(Optional.of(storefront));

    Storefront result = storefrontService.findById(storefrontId);

    assertNotNull(result);
    assertEquals(storefrontId, result.getId());
    verify(storefrontRepository, times(1)).findById(storefrontId);
  }

  @Test
  void shouldThrowResourceNotFoundException_whenFindByIdDoesNotExist() {
    UUID nonExistentId = UUID.randomUUID();
    when(storefrontRepository.findById(nonExistentId)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> storefrontService.findById(nonExistentId));

    verify(storefrontRepository, times(1)).findById(nonExistentId);
  }

  @Test
  void shouldReturnStorefrontDTO_whenGetDTOByIdExists() {
    when(storefrontRepository.findById(storefrontId)).thenReturn(Optional.of(storefront));

    StorefrontDTO result = storefrontService.getDTOById(storefrontId);

    assertEquals(storefront.getPrimaryColor(), result.getPrimaryColor());
    assertEquals(storefront.getIsFirstCollections(), result.getIsFirstCollections());
    verify(storefrontRepository, times(1)).findById(storefrontId);
  }

  @Test
  void shouldUpdateStorefront_whenStorefrontExists() {
    StorefrontDTO dto = new StorefrontDTO();
    dto.setPrimaryColor("#000000");
    dto.setIsFirstCollections(false);

    when(storefrontRepository.findById(storefrontId)).thenReturn(Optional.of(storefront));
    when(storefrontRepository.save(any(Storefront.class))).thenReturn(storefront);

    StorefrontDTO result = storefrontService.updateStorefront(storefrontId, dto);

    assertEquals("#000000", result.getPrimaryColor());
    assertEquals(false, result.getIsFirstCollections());
    verify(storefrontRepository, times(1)).findById(storefrontId);
    verify(storefrontRepository, times(1)).save(any(Storefront.class));
  }

  @Test
  void shouldThrowResourceNotFoundException_whenUpdateNonExistentStorefront() {
    UUID nonExistentId = UUID.randomUUID();
    StorefrontDTO dto = new StorefrontDTO();
    when(storefrontRepository.findById(nonExistentId)).thenReturn(Optional.empty());

    assertThrows(
        ResourceNotFoundException.class,
        () -> storefrontService.updateStorefront(nonExistentId, dto));

    verify(storefrontRepository, times(1)).findById(nonExistentId);
    verify(storefrontRepository, times(0)).save(any(Storefront.class));
  }
}
