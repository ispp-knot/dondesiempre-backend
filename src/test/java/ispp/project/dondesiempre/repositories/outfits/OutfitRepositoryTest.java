package ispp.project.dondesiempre.repositories.outfits;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ispp.project.dondesiempre.models.outfits.Outfit;
import ispp.project.dondesiempre.models.outfits.OutfitProduct;
import ispp.project.dondesiempre.models.products.Product;
import ispp.project.dondesiempre.models.products.ProductType;
import ispp.project.dondesiempre.models.storefronts.Storefront;
import ispp.project.dondesiempre.models.stores.Store;
import ispp.project.dondesiempre.repositories.products.ProductRepository;
import ispp.project.dondesiempre.repositories.products.ProductTypeRepository;
import ispp.project.dondesiempre.repositories.storefronts.StorefrontRepository;
import ispp.project.dondesiempre.repositories.stores.StoreRepository;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.locationtech.jts.geom.impl.CoordinateArraySequence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class OutfitRepositoryTest {
  @Autowired private OutfitRepository outfitRepository;

  @Autowired private StoreRepository storeRepository;

  @Autowired private StorefrontRepository storefrontRepository;

  @Autowired private ProductRepository productRepository;

  @Autowired private OutfitProductRepository outfitProductRepository;

  @Autowired private ProductTypeRepository typeRepository;

  @Test
  void shouldReturnNoOutfits_whenStoreHasNoOutfits() {
    Store store;
    Storefront storefront;

    List<Outfit> result;

    storefront = new Storefront();
    storefrontRepository.save(storefront);

    store = new Store();
    store.setName("Test store");
    store.setAddress("Test address");
    store.setStorefront(storefront);
    store.setLocation(
        new Point(
            new CoordinateArraySequence(new Coordinate[] {new Coordinate(0.0, 0.0)}),
            new GeometryFactory(new PrecisionModel(PrecisionModel.FIXED), 0)));
    store.setAboutUs("Test description");
    store.setOpeningHours("Test opening hours");
    store.setEmail("test@test.com");
    store.setStoreID("testStoreID");
    store.setAcceptsShipping(false);
    store = storeRepository.save(store);

    result = outfitRepository.findByStoreId(store.getId());

    assertTrue(result.isEmpty());
  }

  @Test
  void shouldReturnOneOutfit_whenStoreHasOneOutfit() {
    Store store;
    Storefront storefront;

    ProductType type;
    Product product;

    Outfit outfit;
    OutfitProduct outfitProduct;

    List<Outfit> result;

    storefront = new Storefront();
    storefrontRepository.save(storefront);

    store = new Store();
    store.setName("Test store");
    store.setAddress("Test address");
    store.setStorefront(storefront);
    store.setLocation(
        new Point(
            new CoordinateArraySequence(new Coordinate[] {new Coordinate(0.0, 0.0)}),
            new GeometryFactory(new PrecisionModel(PrecisionModel.FIXED), 0)));
    store.setAboutUs("Test description");
    store.setOpeningHours("Test opening hours");
    store.setEmail("test@test.com");
    store.setStoreID("testStoreID");
    store.setAcceptsShipping(false);
    store = storeRepository.save(store);

    type = new ProductType();
    type.setType("Test type");
    type = typeRepository.save(type);

    product = new Product();
    product.setName("Test product");
    product.setPriceInCents(1000);
    product.setDiscountedPriceInCents(1000);
    product.setType(type);
    product.setStore(store);
    product = productRepository.save(product);

    outfit = new Outfit();
    outfit.setName("Test outfit");
    outfit.setDiscountedPriceInCents(100);
    outfit.setIndex(0);
    outfit.setStorefront(storefront);
    outfit = outfitRepository.save(outfit);

    outfitProduct = new OutfitProduct();
    outfitProduct.setIndex(0);
    outfitProduct.setProduct(product);
    outfitProduct.setOutfit(outfit);
    outfitProduct = outfitProductRepository.save(outfitProduct);

    result = outfitRepository.findByStoreId(store.getId());

    assertEquals(1, result.size());
    assertTrue(result.contains(outfit));
  }

  @Test
  void shouldReturnTwoOutfits_whenStoreHasTwoOutfits() {
    Store store1, store2;
    Storefront storefront1, storefront2;

    ProductType type;
    Integer numProducts;

    List<Product> products;

    Outfit outfit1, outfit2, outfit3;
    OutfitProduct outfitProduct;

    List<Outfit> result;

    storefront1 = new Storefront();
    storefrontRepository.save(storefront1);

    storefront2 = new Storefront();
    storefrontRepository.save(storefront2);

    store1 = new Store();
    store1.setName("Test store 1");
    store1.setAddress("Test address");
    store1.setStorefront(storefront1);
    store1.setLocation(
        new Point(
            new CoordinateArraySequence(new Coordinate[] {new Coordinate(0.0, 0.0)}),
            new GeometryFactory(new PrecisionModel(PrecisionModel.FIXED), 0)));
    store1.setAboutUs("Test description");
    store1.setOpeningHours("Test opening hours");
    store1.setEmail("test@test.com");
    store1.setStoreID("testStoreID");
    store1.setAcceptsShipping(false);
    store1 = storeRepository.save(store1);

    store2 = new Store();
    store2.setName("Test store 2");
    store2.setAddress("Test address");
    store2.setStorefront(storefront2);
    store2.setLocation(
        new Point(
            new CoordinateArraySequence(new Coordinate[] {new Coordinate(0.0, 0.0)}),
            new GeometryFactory(new PrecisionModel(PrecisionModel.FIXED), 0)));
    store2.setAboutUs("Test description");
    store2.setOpeningHours("Test opening hours");
    store2.setEmail("test@test.com");
    store2.setStoreID("testStoreID");
    store2.setAcceptsShipping(false);
    store2 = storeRepository.save(store2);

    type = new ProductType();
    type.setType("Test type");
    type = typeRepository.save(type);

    outfit1 = new Outfit();
    outfit1.setName("Test outfit 1");
    outfit1.setDiscountedPriceInCents(100);
    outfit1.setIndex(0);
    outfit1.setStorefront(storefront1);
    outfit1 = outfitRepository.save(outfit1);

    outfit2 = new Outfit();
    outfit2.setName("Test outfit 2");
    outfit2.setDiscountedPriceInCents(100);
    outfit2.setIndex(1);
    outfit2.setStorefront(storefront1);
    outfit2 = outfitRepository.save(outfit2);

    outfit3 = new Outfit();
    outfit3.setName("Test outfit 3");
    outfit3.setDiscountedPriceInCents(100);
    outfit3.setIndex(0);
    outfit3.setStorefront(storefront2);
    outfit3 = outfitRepository.save(outfit3);

    numProducts = 4;
    products = new ArrayList<>();

    for (Integer i = 0; i < numProducts; i++) {
      Product product;

      product = new Product();
      product.setName("Test product" + i);
      product.setPriceInCents(i * 1000); // Set price in cents (e.g., 1000 = $10.00)
      product.setDiscountedPriceInCents(i * 1000); // Set discounted price in cents
      product.setType(type);
      product = productRepository.save(product);

      products.add(product);
    }
    products.get(0).setStore(store1);

    outfitProduct = new OutfitProduct();
    outfitProduct.setIndex(0);
    outfitProduct.setProduct(products.get(0));
    outfitProduct.setOutfit(outfit1);
    outfitProduct = outfitProductRepository.save(outfitProduct);

    products.get(1).setStore(store1);

    outfitProduct = new OutfitProduct();
    outfitProduct.setIndex(1);
    outfitProduct.setProduct(products.get(2));
    outfitProduct.setOutfit(outfit2);
    outfitProduct = outfitProductRepository.save(outfitProduct);

    products.get(2).setStore(store1);

    outfitProduct = new OutfitProduct();
    outfitProduct.setIndex(0);
    outfitProduct.setProduct(products.get(1));
    outfitProduct.setOutfit(outfit2);
    outfitProduct = outfitProductRepository.save(outfitProduct);

    products.get(3).setStore(store2);

    outfitProduct = new OutfitProduct();
    outfitProduct.setIndex(0);
    outfitProduct.setProduct(products.get(3));
    outfitProduct.setOutfit(outfit3);
    outfitProduct = outfitProductRepository.save(outfitProduct);

    result = outfitRepository.findByStoreId(store1.getId());

    assertEquals(2, result.size());

    assertTrue(result.contains(outfit1));
    assertTrue(result.contains(outfit2));

    assertEquals(0, result.get(0).getIndex());
    assertEquals(1, result.get(1).getIndex());

    assertFalse(result.contains(outfit3));
  }
}
