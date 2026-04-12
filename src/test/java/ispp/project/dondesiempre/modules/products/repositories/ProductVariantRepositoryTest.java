package ispp.project.dondesiempre.modules.products.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ispp.project.dondesiempre.config.coordinates.GeometryFactoryConfig;
import ispp.project.dondesiempre.modules.auth.models.User;
import ispp.project.dondesiempre.modules.auth.repositories.UserRepository;
import ispp.project.dondesiempre.modules.products.models.Product;
import ispp.project.dondesiempre.modules.products.models.ProductColor;
import ispp.project.dondesiempre.modules.products.models.ProductSize;
import ispp.project.dondesiempre.modules.products.models.ProductType;
import ispp.project.dondesiempre.modules.products.models.ProductVariant;
import ispp.project.dondesiempre.modules.stores.models.Store;
import ispp.project.dondesiempre.modules.stores.models.Storefront;
import ispp.project.dondesiempre.modules.stores.repositories.StoreRepository;
import ispp.project.dondesiempre.modules.stores.repositories.StorefrontRepository;
import ispp.project.dondesiempre.utils.cloudinary.CoordinatesService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Import({CoordinatesService.class, GeometryFactoryConfig.class})
public class ProductVariantRepositoryTest {

  @Autowired private ProductVariantRepository productVariantRepository;
  @Autowired private ProductRepository productRepository;
  @Autowired private ProductTypeRepository productTypeRepository;
  @Autowired private ProductColorRepository productColorRepository;
  @Autowired private ProductSizeRepository productSizeRepository;
  @Autowired private StoreRepository storeRepository;
  @Autowired private StorefrontRepository storefrontRepository;
  @Autowired private UserRepository userRepository;
  @Autowired private CoordinatesService coordinatesService;

  private Product product;
  private ProductSize size;
  private ProductColor color;

  @BeforeEach
  void setUp() {
    User user = new User();
    user.setEmail("variant-repo-test@test.com");
    user.setPassword("password");
    userRepository.save(user);

    Storefront storefront = new Storefront();
    storefrontRepository.save(storefront);

    Store store = new Store();
    store.setName("Test Store");
    store.setEmail("test@test.com");
    store.setAddress("Test address");
    store.setOpeningHours("9-5");
    store.setLocation(coordinatesService.createPoint(0.0, 0.0));
    store.setStorefront(storefront);
    store.setUser(user);
    store.setAccountId("acc_AAAAA");
    storeRepository.save(store);

    ProductType type = new ProductType();
    type.setType("Test Type");
    type = productTypeRepository.save(type);

    product = new Product();
    product.setName("Test Product");
    product.setPriceInCents(1000);
    product.setType(type);
    product.setStore(store);
    product = productRepository.save(product);

    size = new ProductSize();
    size.setSize("M");
    size = productSizeRepository.save(size);

    color = new ProductColor();
    color.setColor("Red");
    color = productColorRepository.save(color);

    ProductVariant variant1 = new ProductVariant();
    variant1.setProduct(product);
    variant1.setSize(size);
    variant1.setColor(color);
    variant1.setIsAvailable(true);
    productVariantRepository.save(variant1);

    ProductSize size2 = new ProductSize();
    size2.setSize("L");
    size2 = productSizeRepository.save(size2);

    ProductVariant variant2 = new ProductVariant();
    variant2.setProduct(product);
    variant2.setSize(size2);
    variant2.setColor(color);
    variant2.setIsAvailable(false);
    productVariantRepository.save(variant2);
  }

  @Test
  public void shouldFindAllVariantsByProductId() {
    List<ProductVariant> variants = productVariantRepository.findByProductId(product.getId());

    assertNotNull(variants);
    assertEquals(2, variants.size());
    assertTrue(variants.stream().allMatch(v -> v.getProduct().getId().equals(product.getId())));
  }

  @Test
  public void shouldFindAvailableVariantsByProductId() {
    List<ProductVariant> variants =
        productVariantRepository.findByProductIdAndIsAvailableTrue(product.getId());

    assertNotNull(variants);
    assertEquals(1, variants.size());
    assertTrue(variants.stream().allMatch(ProductVariant::getIsAvailable));
  }

  @Test
  public void shouldFindVariantsBySizeId() {
    List<ProductVariant> variants = productVariantRepository.findBySizeId(size.getId());

    assertNotNull(variants);
    assertFalse(variants.isEmpty());
    assertTrue(variants.stream().allMatch(v -> v.getSize().getId().equals(size.getId())));
  }

  @Test
  public void shouldFindVariantsByColorId() {
    List<ProductVariant> variants = productVariantRepository.findByColorId(color.getId());

    assertNotNull(variants);
    assertEquals(2, variants.size());
    assertTrue(variants.stream().allMatch(v -> v.getColor().getId().equals(color.getId())));
  }

  @Test
  public void shouldReturnEmptyList_WhenNoVariantsFoundForProductId() {
    List<ProductVariant> variants = productVariantRepository.findByProductId(null);

    assertNotNull(variants);
    assertTrue(variants.isEmpty());
  }

  @Test
  public void shouldSaveAndRetrieveProductVariant() {
    List<ProductVariant> allVariants = productVariantRepository.findAll();

    assertNotNull(allVariants);
    assertFalse(allVariants.isEmpty());

    ProductVariant variant = allVariants.get(0);
    assertNotNull(variant.getId());
    assertNotNull(variant.getProduct());
    assertNotNull(variant.getSize());
    assertNotNull(variant.getColor());
  }

  @Test
  public void shouldNotReturnDeletedVariantsWhenFindByProductId() {
    List<ProductVariant> initialVariants =
        productVariantRepository.findByProductIdAndIsDeletedIsFalse(product.getId());
    int initialCount = initialVariants.size();

    if (!initialVariants.isEmpty()) {
      ProductVariant variantToDelete = initialVariants.get(0);
      variantToDelete.setDeleted(true);
      productVariantRepository.save(variantToDelete);

      List<ProductVariant> updatedVariants =
          productVariantRepository.findByProductIdAndIsDeletedIsFalse(product.getId());
      assertEquals(initialCount - 1, updatedVariants.size());
    }
  }

  @Test
  public void shouldNotReturnDeletedVariantsWhenFindAvailable() {
    List<ProductVariant> initialAvailableVariants =
        productVariantRepository.findByProductIdAndIsAvailableTrueAndIsDeletedIsFalse(
            product.getId());
    int initialCount = initialAvailableVariants.size();

    if (!initialAvailableVariants.isEmpty()) {
      ProductVariant variantToDelete = initialAvailableVariants.get(0);
      variantToDelete.setDeleted(true);
      productVariantRepository.save(variantToDelete);

      List<ProductVariant> updatedVariants =
          productVariantRepository.findByProductIdAndIsAvailableTrueAndIsDeletedIsFalse(
              product.getId());
      assertEquals(initialCount - 1, updatedVariants.size());
    }
  }

  @Test
  public void shouldFindVariantByIdAndNotDeleted() {
    List<ProductVariant> allVariants =
        productVariantRepository.findByProductIdAndIsDeletedIsFalse(product.getId());

    if (!allVariants.isEmpty()) {
      ProductVariant variant = allVariants.get(0);
      var foundVariant = productVariantRepository.findByIdAndIsDeletedIsFalse(variant.getId());

      assertTrue(foundVariant.isPresent());
      assertEquals(variant.getId(), foundVariant.get().getId());
      assertFalse(foundVariant.get().isDeleted());
    }
  }

  @Test
  public void shouldNotFindDeletedVariantById() {
    List<ProductVariant> allVariants =
        productVariantRepository.findByProductIdAndIsDeletedIsFalse(product.getId());

    if (!allVariants.isEmpty()) {
      ProductVariant variant = allVariants.get(0);
      variant.setDeleted(true);
      productVariantRepository.save(variant);

      var foundVariant = productVariantRepository.findByIdAndIsDeletedIsFalse(variant.getId());
      assertFalse(foundVariant.isPresent());
    }
  }

  @Test
  public void shouldFindAllNonDeletedVariants() {
    List<ProductVariant> allVariants = productVariantRepository.findByIsDeletedIsFalse();

    assertNotNull(allVariants);
    assertFalse(allVariants.isEmpty());
    for (ProductVariant variant : allVariants) {
      assertFalse(variant.isDeleted());
    }
  }
}
