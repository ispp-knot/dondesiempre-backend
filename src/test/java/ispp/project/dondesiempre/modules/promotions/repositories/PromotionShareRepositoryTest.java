package ispp.project.dondesiempre.modules.promotions.repositories;

import ispp.project.dondesiempre.config.coordinates.GeometryFactoryConfig;
import ispp.project.dondesiempre.modules.auth.models.User;
import ispp.project.dondesiempre.modules.auth.repositories.UserRepository;
import ispp.project.dondesiempre.modules.promotions.models.Promotion;
import ispp.project.dondesiempre.modules.promotions.models.PromotionShare;
import ispp.project.dondesiempre.modules.stores.models.Store;
import ispp.project.dondesiempre.modules.stores.models.Storefront;
import ispp.project.dondesiempre.modules.stores.repositories.StoreRepository;
import ispp.project.dondesiempre.modules.stores.repositories.StorefrontRepository;
import ispp.project.dondesiempre.utils.cloudinary.CoordinatesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({CoordinatesService.class, GeometryFactoryConfig.class})
public class PromotionShareRepositoryTest {

    @Autowired private StoreRepository storeRepository;
    @Autowired private UserRepository userRepository;
    @Autowired CoordinatesService coordinatesService;
    @Autowired private StorefrontRepository storefrontRepository;
    @Autowired private PromotionRepository promotionRepository;
    @Autowired private PromotionShareRepository promotionShareRepository;

    private Promotion savedPromotion;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setEmail("product-repo-test@test.com");
        user.setPassword("password");
        userRepository.save(user);

        Storefront storefront = new Storefront();
        storefrontRepository.save(storefront);

        Store store = new Store();
        store.setName("Test Store");
        store.setEmail("test@test.com");
        store.setAddress("Test address");
        store.setOpeningHours("9-5");
        store.setAcceptsShipping(false);
        store.setLocation(coordinatesService.createPoint(0.0, 0.0));
        store.setStorefront(storefront);
        store.setUser(user);
        storeRepository.save(store);

        savedPromotion = new Promotion();
        savedPromotion.setName("Test Promotion");
        savedPromotion.setStartDate(LocalDate.now());
        savedPromotion.setEndDate(LocalDate.now().plusDays(7));
        savedPromotion.setDiscountPercentage(20);
        savedPromotion.setActive(true);
        savedPromotion.setStore(store);
        promotionRepository.save(savedPromotion);
    }

    private void createPromotionShare() {
        PromotionShare share = new PromotionShare();
        share.setDate(LocalDate.now().minusDays(7));
        share.setPromotion(savedPromotion);
        promotionShareRepository.save(share);
    }

    private void createOldPromotionShare() {
        PromotionShare share = new PromotionShare();
        share.setDate(LocalDate.now().minusMonths(1).minusDays(1));
        share.setPromotion(savedPromotion);
        promotionShareRepository.save(share);
    }

    @Test
    public void shouldReturnNotExceededLimit() {
        boolean exceedsLimit = promotionShareRepository.hasReachedMonthlyLimit(savedPromotion, LocalDate.now());
        assertFalse(exceedsLimit);
    }

    @Test
    public void shouldReturnNotExceededLimit_whenMonthHasPassed() {
        for (int i = 0; i < 5; i++) {
            createOldPromotionShare();
        }
        boolean exceedsLimit = promotionShareRepository.hasReachedMonthlyLimit(
                savedPromotion,
                LocalDate.now().minusMonths(1)
        );
        assertFalse(exceedsLimit);
    }

    @Test
    public void shouldReturnNotExceededLimit_whenPremiumLimitNotSurpassed() {
        for (int i = 0; i < 1; i++) {
            createPromotionShare();
        }
        boolean exceedsLimit = promotionShareRepository.hasReachedMonthlyLimit(
                savedPromotion,
                LocalDate.now().minusMonths(1)
        );
        assertFalse(exceedsLimit);
    }

    @Test
    public void shouldReturnExceededLimit() {
        for (int i = 0; i < 3; i++) {
            createPromotionShare();
        }
        boolean exceedsLimit = promotionShareRepository.hasReachedMonthlyLimit(
                savedPromotion,
                LocalDate.now().minusMonths(1)
        );
        assertTrue(exceedsLimit);
    }
}
