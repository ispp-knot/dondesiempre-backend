package ispp.project.dondesiempre.services.storefronts;

import ispp.project.dondesiempre.models.storefronts.Storefront;
import ispp.project.dondesiempre.models.stores.Store;
import ispp.project.dondesiempre.repositories.storefronts.StorefrontRepository;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StorefrontService {

  private final StorefrontRepository repository;

  public StorefrontService(StorefrontRepository repository) {
    this.repository = repository;
  }

  @Transactional
  // TODO: This method should be called when a new store is created, and it should be part of the
  // same transaction as the store creation.
  public Storefront create(Store store) {
    Objects.requireNonNull(store, "store must not be null");

    Storefront storefront = new Storefront();
    storefront.setStore(store);

    String defaultImagePath = "/defaults/storefront-default-banner.jpg";
    try (InputStream defaultImageStream = this.getClass().getResourceAsStream(defaultImagePath)) {
      if (defaultImageStream != null) {
        byte[] img = defaultImageStream.readAllBytes();
        storefront.setBannerImage(img);
        String filename = defaultImagePath.substring(defaultImagePath.lastIndexOf('/') + 1);
        storefront.setBannerImageFilename(filename);
        storefront.setBannerImageContentType("image/jpeg");
      }
    } catch (IOException e) {
      throw new RuntimeException("Failed to load default storefront banner image", e);
    }

    return repository.save(storefront);
  }
}
