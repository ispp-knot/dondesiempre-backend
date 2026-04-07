package ispp.project.dondesiempre.modules.promotions.services;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ispp.project.dondesiempre.modules.auth.models.User;
import ispp.project.dondesiempre.modules.auth.repositories.UserRepository;
import ispp.project.dondesiempre.modules.auth.services.AuthService;
import ispp.project.dondesiempre.modules.common.exceptions.LimitExceededException;
import ispp.project.dondesiempre.modules.promotions.models.Promotion;
import ispp.project.dondesiempre.modules.promotions.repositories.PromotionRepository;
import ispp.project.dondesiempre.modules.promotions.repositories.PromotionShareRepository;
import ispp.project.dondesiempre.modules.stores.models.Store;
import ispp.project.dondesiempre.modules.stores.models.Storefront;
import ispp.project.dondesiempre.modules.stores.repositories.StoreRepository;
import ispp.project.dondesiempre.utils.cloudinary.CoordinatesService;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
public class PromotionShareServiceTest {

  @Autowired private PromotionRepository promotionRepository;
  @Autowired private StoreRepository storeRepository;
  @Autowired private UserRepository userRepository;
  @MockitoBean private AuthService authService;
  @Autowired CoordinatesService coordinatesService;
  @Autowired private PromotionShareRepository promotionShareRepository;

  @Autowired private PromotionShareService promotionShareService;

  private Storefront createStorefront() {
    Storefront storefront = new Storefront();

    storefront.setPrimaryColor("#c65a3a");
    storefront.setSecondaryColor("#19756a");
    return storefront;
  }

  private Store createAndSaveStore(boolean isPremium) {

    User user = new User();
    user.setEmail("user@example.com");
    user.setPassword("password");
    user = userRepository.save(user);

    Store store = new Store();
    store.setName("Test Store");
    store.setEmail("store@example.com");
    store.setLocation(coordinatesService.createPoint(0.0, 0.0));
    store.setAddress("123 Test Street");
    store.setOpeningHours("9am - 5pm");
    store.setStorefront(createStorefront());
    store.setUser(user);
    store.setPremiumPlan(isPremium);
    return storeRepository.save(store);
  }

  private Promotion createPromotion(Store store) {
    Promotion promotion = new Promotion();
    promotion.setName("Test Promotion");
    promotion.setDiscountPercentage(20);
    promotion.setActive(true);
    promotion.setStore(store);
    promotion.setStartDate(LocalDate.now());
    promotion.setEndDate(LocalDate.now().plusMonths(1));
    return promotionRepository.save(promotion);
  }

  @Test
  public void shouldCreateNewPromotionShare_whenIsValid() {
    Store store = createAndSaveStore(false);
    Promotion promotion = createPromotion(store);
    long before = promotionShareRepository.count();
    promotionShareService.save(promotion.getId());
    long after = promotionShareRepository.count();
    assertEquals(before + 1, after);
  }

  @Test
  public void shouldThrowLimitException_whenLimitExceeded() {
    Store store = createAndSaveStore(false);
    Promotion promotion = createPromotion(store);

    for (int i = 0; i < 2; i++) {
      promotionShareService.save(promotion.getId());
    }

    assertThrows(LimitExceededException.class, () -> promotionShareService.save(promotion.getId()));
  }

  @Test
  public void shouldAllowPromotionShare_whenLimitExceededByPremiumStore() {
    Store store = createAndSaveStore(true);
    Promotion promotion = createPromotion(store);

    for (int i = 0; i < 2; i++) {
      promotionShareService.save(promotion.getId());
    }

    assertDoesNotThrow(() -> promotionShareService.save(promotion.getId()));
  }
}
