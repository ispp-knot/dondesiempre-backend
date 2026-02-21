package ispp.project.dondesiempre.services.storefronts;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import ispp.project.dondesiempre.models.storefronts.Storefront;
import ispp.project.dondesiempre.models.stores.Store;
import ispp.project.dondesiempre.repositories.storefronts.StorefrontRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StorefrontServiceTest {

  @Mock StorefrontRepository repository;

  @InjectMocks StorefrontService service;

  @Test
  void createWithStoreLoadsDefaultImage() {
    Store store = new Store();
    store.setId(1);
    store.setName("My Store");
    when(repository.save(any(Storefront.class))).thenAnswer(i -> i.getArgument(0));

    Storefront result = service.create(store);

    assertSame(store, result.getStore());
    assertNotNull(result.getBannerImage(), "banner image should be set from resource");
    assertEquals("storefront-default-banner.jpg", result.getBannerImageFilename());
    assertEquals("image/jpeg", result.getBannerImageContentType());

    verify(repository, times(1)).save(any(Storefront.class));
  }

  @Test
  void createNullStoreThrows() {
    assertThrows(NullPointerException.class, () -> service.create(null));
    verifyNoInteractions(repository);
  }
}
