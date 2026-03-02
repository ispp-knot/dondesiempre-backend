package ispp.project.dondesiempre.services;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import ispp.project.dondesiempre.exceptions.ResourceNotFoundException;
import ispp.project.dondesiempre.models.storefronts.Storefront;
import ispp.project.dondesiempre.models.storefronts.dto.StorefrontDTO;
import ispp.project.dondesiempre.repositories.storefronts.StorefrontRepository;
import ispp.project.dondesiempre.services.storefronts.StorefrontService;

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

    when(storefrontRepository.findById(storefrontId)).thenReturn(Optional.of(storefront));
    when(storefrontRepository.save(any(Storefront.class))).thenAnswer(i -> i.getArgument(0));

    StorefrontDTO result = storefrontService.updateStorefront(storefrontId, dto);

    assertEquals("#000000", result.getPrimaryColor());
    assertEquals(false, result.getIsFirstCollections());
    verify(storefrontRepository, times(1)).findById(storefrontId);
    verify(storefrontRepository, times(1)).save(any(Storefront.class));
  }
}