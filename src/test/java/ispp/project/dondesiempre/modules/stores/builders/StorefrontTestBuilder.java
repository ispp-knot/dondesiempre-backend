package ispp.project.dondesiempre.modules.stores.builders;

import ispp.project.dondesiempre.modules.stores.models.Storefront;
import ispp.project.dondesiempre.modules.stores.repositories.StorefrontRepository;

import java.util.UUID;

import ispp.project.dondesiempre.mockEntities.RandomBasicDataGenerator;
import lombok.Builder;

/**
 * Test Builder for {@link Storefront}.
 * Provides default colors and optional banner URL.
 */
@Builder
public class StorefrontTestBuilder {

    @Builder.Default
    UUID id = null;

    @Builder.Default
    private Boolean isFirstCollections = Boolean.TRUE;

    @Builder.Default
    private String primaryColor = RandomBasicDataGenerator.generateRandomColor();

    @Builder.Default
    private String secondaryColor = RandomBasicDataGenerator.generateRandomColor();

    @Builder.Default
    private String bannerImageUrl = RandomBasicDataGenerator.generateRandomUrl();

    /**
     * Builds an in-memory {@link Storefront}.
     */
    public Storefront entity() {
        Storefront storefront = new Storefront();
        storefront.setId(id);
        storefront.setIsFirstCollections(isFirstCollections);
        storefront.setPrimaryColor(primaryColor);
        storefront.setSecondaryColor(secondaryColor);
        storefront.setBannerImageUrl(bannerImageUrl);
        return storefront;
    }

    /**
     * Persists the storefront using the provided repository.
     *
     * @param storefrontRepository repository to save the storefront
     * @return persisted {@link Storefront}
     */
    public Storefront persist(StorefrontRepository storefrontRepository) {
        return storefrontRepository.save(this.entity());
    }
}