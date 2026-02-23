package ispp.project.dondesiempre.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ispp.project.dondesiempre.models.products.Category;
import ispp.project.dondesiempre.models.storefronts.Storefront;
import ispp.project.dondesiempre.models.stores.Store;
import ispp.project.dondesiempre.repositories.products.CategoryRepository;
import ispp.project.dondesiempre.repositories.storefronts.StorefrontRepository;
import ispp.project.dondesiempre.repositories.stores.StoreRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@ActiveProfiles("dev")
class CategoryRepositoryTest {

  @Autowired private CategoryRepository categoryRepository;

  @Autowired private StoreRepository storeRepository;

  @Autowired private StorefrontRepository storefrontRepository;

  private Store crearStore() {
    Storefront storefront = new Storefront();
    storefrontRepository.save(storefront);

    GeometryFactory gf = new GeometryFactory(new PrecisionModel(), 4326);
    Point location = gf.createPoint(new Coordinate(-5.9845, 37.3891));

    Store store = new Store();
    store.setName("Tienda Test");
    store.setEmail("test@tienda.com");
    store.setStoreID("STORE-TEST-001");
    store.setLocation(location);
    store.setAddress("Calle Test 1");
    store.setOpeningHours("L-V 10:00-20:00");
    store.setAcceptsShipping(false);
    store.setStorefront(storefront);
    return storeRepository.save(store);
  }

  @Test
  void testCreate() {
    Store store = crearStore();
    Category category = new Category();
    category.setName("Camisetas");
    category.setDescription("Ropa de verano");
    category.setStore(store);

    Category saved = categoryRepository.save(category);

    assertNotNull(saved.getId());
    assertEquals("Camisetas", saved.getName());
  }

  @Test
  void testRead() {
    Store store = crearStore();
    Category category = new Category();
    category.setName("Pantalones");
    category.setStore(store);
    Category saved = categoryRepository.save(category);

    Category found = categoryRepository.findById(saved.getId()).orElse(null);

    assertNotNull(found);
    assertEquals(saved.getId(), found.getId());
    assertEquals("Pantalones", found.getName());
  }

  @Test
  void testUpdate() {
    Store store = crearStore();
    Category category = new Category();
    category.setName("Zapatos");
    category.setStore(store);
    Category saved = categoryRepository.save(category);

    saved.setName("Zapatillas");
    Category updated = categoryRepository.save(saved);

    assertEquals("Zapatillas", updated.getName());
    assertEquals(saved.getId(), updated.getId());
  }

  @Test
  void testDelete() {
    Store store = crearStore();
    Category category = new Category();
    category.setName("Abrigos");
    category.setStore(store);
    Category saved = categoryRepository.save(category);
    Integer id = saved.getId();

    categoryRepository.deleteById(id);

    assertTrue(categoryRepository.findById(id).isEmpty());
  }

  @Test
  void testFindByStoreId() {
    Store store = crearStore();
    Category category = new Category();
    category.setName("Vestidos");
    category.setStore(store);
    categoryRepository.save(category);

    List<Category> categories = categoryRepository.findByStoreId(store.getId());

    assertFalse(categories.isEmpty());
    assertTrue(categories.stream().allMatch(c -> c.getStore().getId().equals(store.getId())));
  }

  @Test
  void testExistsByNameAndStoreId_true() {
    Store store = crearStore();
    Category category = new Category();
    category.setName("Accesorios");
    category.setStore(store);
    categoryRepository.save(category);

    assertTrue(categoryRepository.existsByNameAndStoreId("Accesorios", store.getId()));
  }

  @Test
  void testExistsByNameAndStoreId_false() {
    Store store = crearStore();

    assertFalse(categoryRepository.existsByNameAndStoreId("Inexistente", store.getId()));
  }

  @Test
  void testSameNameDifferentStores() {
    Store store1 = crearStore();
    Store store2 = crearStore();

    Category cat1 = new Category();
    cat1.setName("Camisetas");
    cat1.setStore(store1);
    categoryRepository.save(cat1);

    Category cat2 = new Category();
    cat2.setName("Camisetas");
    cat2.setStore(store2);
    categoryRepository.save(cat2);

    assertTrue(categoryRepository.existsByNameAndStoreId("Camisetas", store1.getId()));
    assertTrue(categoryRepository.existsByNameAndStoreId("Camisetas", store2.getId()));
  }

  @Test
  void testFindByStoreId_empty() {
    Store store2 = crearStore();

    List<Category> categories = categoryRepository.findByStoreId(store2.getId());

    assertNotNull(categories);
    assertTrue(categories.isEmpty());
  }
}
