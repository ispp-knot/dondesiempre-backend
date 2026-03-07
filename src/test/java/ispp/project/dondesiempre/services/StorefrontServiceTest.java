package ispp.project.dondesiempre.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ispp.project.dondesiempre.modules.common.exceptions.ResourceNotFoundException;
import ispp.project.dondesiempre.modules.stores.dtos.StorefrontDTO;
import ispp.project.dondesiempre.modules.stores.models.Storefront;
import ispp.project.dondesiempre.modules.stores.repositories.StorefrontRepository;
import ispp.project.dondesiempre.modules.stores.services.StorefrontService;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

@ExtendWith(MockitoExtension.class)
public class StorefrontServiceTest {

  @Mock private StorefrontRepository storefrontRepository;
  @Mock private ApplicationContext applicationContext;

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
  void findById_shouldReturnStorefront_whenStorefrontExists() {
    when(storefrontRepository.findById(storefrontId)).thenReturn(Optional.of(storefront));

    Storefront result = storefrontService.findById(storefrontId);

    assertNotNull(result);
    assertEquals(storefrontId, result.getId());
    verify(storefrontRepository, times(1)).findById(storefrontId);
  }

  @Test
  void findById_shouldThrowResourceNotFoundException_whenStorefrontDoesNotExist() {
    UUID nonExistentId = UUID.randomUUID();
    when(storefrontRepository.findById(nonExistentId)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> storefrontService.findById(nonExistentId));

    verify(storefrontRepository, times(1)).findById(nonExistentId);
  }

  @Test
  void updateStorefront_shouldUpdateStorefront_whenStorefrontExists() {
    StorefrontDTO dto = new StorefrontDTO();
    dto.setPrimaryColor("#000000");
    dto.setIsFirstCollections(false);

    when(applicationContext.getBean(StorefrontService.class)).thenReturn(storefrontService);

    when(storefrontRepository.findById(storefrontId)).thenReturn(Optional.of(storefront));
    when(storefrontRepository.save(any(Storefront.class))).thenAnswer(i -> i.getArgument(0));

    StorefrontDTO result = storefrontService.updateStorefront(storefrontId, dto);

    assertEquals("#000000", result.getPrimaryColor());
    assertEquals(false, result.getIsFirstCollections());

    verify(storefrontRepository, times(1)).findById(storefrontId);
    verify(storefrontRepository, times(1)).save(any(Storefront.class));
  }
}
