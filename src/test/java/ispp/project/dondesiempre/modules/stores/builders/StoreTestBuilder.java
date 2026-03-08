package ispp.project.dondesiempre.modules.stores.builders;

import java.util.UUID;

import org.locationtech.jts.geom.Point;

import ispp.project.dondesiempre.mockEntities.RandomBasicDataGenerator;
import ispp.project.dondesiempre.modules.auth.builders.UserTestBuilder;
import ispp.project.dondesiempre.modules.auth.models.User;
import ispp.project.dondesiempre.modules.auth.repositories.UserRepository;
import ispp.project.dondesiempre.modules.stores.models.Store;
import ispp.project.dondesiempre.modules.stores.models.Storefront;
import ispp.project.dondesiempre.modules.stores.repositories.StoreRepository;
import ispp.project.dondesiempre.modules.stores.repositories.StorefrontRepository;
import lombok.Builder;

/**
 * Test data builder for {@link Store} entity.
 * Generates random defaults for all fields, but allows overrides.
 * Useful for creating stores in tests.
 */
@Builder
public class StoreTestBuilder {

    @Builder.Default
    UUID id = null;

    @Builder.Default
    String name = RandomBasicDataGenerator.generateRandomName("store");

    @Builder.Default
    String email = RandomBasicDataGenerator.generateRandomEmail("email");

    @Builder.Default
    String storeID = "BORRAR ESTO CUANDO SE QUITE EL STORE ID";

    @Builder.Default
    Point location = RandomBasicDataGenerator.generateRandomPoint();

    @Builder.Default
    String address = RandomBasicDataGenerator.generateRandomName("address");

    @Builder.Default
    String openingHours = "9:00-21:00";

    @Builder.Default
    String phone = RandomBasicDataGenerator.generateRandomPhone();

    @Builder.Default
    String aboutUs = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Curabitur ac eros dignissim, volutpat libero eget, commodo velit. Ut fringilla rhoncus rutrum. Proin vulputate auctor turpis ac venenatis. Maecenas ultricies volutpat erat non porttitor. Sed consequat massa sed purus volutpat accumsan.";

    @Builder.Default
    Boolean acceptsShipping = true;

    @Builder.Default
    private Storefront storefront = StorefrontTestBuilder.builder().build().entity();

    @Builder.Default
    private User user = UserTestBuilder.builder().build().entity();

    /**
     * Builds a {@link Store} object with defaults for all fields.
     *
     * @return fully initialized {@link Store} object
     */
    public Store entity() {
        Store store = new Store();
        store.setId(id);
        store.setName(name);
        store.setEmail(email);
        store.setStoreID(storeID);
        store.setLocation(location);
        store.setAddress(address);
        store.setOpeningHours(openingHours);
        store.setPhone(phone);
        store.setAboutUs(aboutUs);
        store.setAcceptsShipping(acceptsShipping);
        store.setUser(user);
        store.setStorefront(storefront);

        return store;
    }

    /**
     * Persists the store and its dependencies in the DB.
     *
     * @param userRepo       UserRepository
     * @param storeRepo      StoreRepository
     * @param storefrontRepo StorefrontRepository
     * @return the persisted {@link Store}
     */
    public Store persist(UserRepository userRepo, StoreRepository storeRepo, StorefrontRepository storefrontRepo) {

        Store store = this.entity();
        user = userRepo.save(store.getUser());
        store.setUser(user);

        storefront = storefrontRepo.save(store.getStorefront());
        store.setStorefront(storefront);

        return storeRepo.save(store);
    }

}
