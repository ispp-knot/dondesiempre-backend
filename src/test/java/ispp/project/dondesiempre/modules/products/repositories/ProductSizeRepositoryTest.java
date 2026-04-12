package ispp.project.dondesiempre.modules.products.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ispp.project.dondesiempre.modules.products.models.ProductSize;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class ProductSizeRepositoryTest {

  @Autowired private ProductSizeRepository productSizeRepository;

  private ProductSize testSize;

  @BeforeEach
  void setUp() {
    testSize = new ProductSize();
    testSize.setSize("M");
    testSize = productSizeRepository.save(testSize);
  }

  @Test
  void shouldSaveProductSize() {
    ProductSize size = new ProductSize();
    size.setSize("L");
    ProductSize savedSize = productSizeRepository.save(size);

    assertNotNull(savedSize);
    assertNotNull(savedSize.getId());
    assertEquals("L", savedSize.getSize());
  }

  @Test
  void shouldFindProductSizeById() {
    Optional<ProductSize> foundSize = productSizeRepository.findById(testSize.getId());

    assertTrue(foundSize.isPresent());
    assertEquals(testSize.getId(), foundSize.get().getId());
    assertEquals("M", foundSize.get().getSize());
  }

  @Test
  void shouldReturnEmptyWhenProductSizeNotFound() {
    UUID randomId = UUID.randomUUID();
    Optional<ProductSize> foundSize = productSizeRepository.findById(randomId);

    assertFalse(foundSize.isPresent());
  }

  @Test
  void shouldFindAllProductSizes() {
    ProductSize size2 = new ProductSize();
    size2.setSize("S");
    productSizeRepository.save(size2);

    ProductSize size3 = new ProductSize();
    size3.setSize("XL");
    productSizeRepository.save(size3);

    List<ProductSize> sizes = productSizeRepository.findAll();

    assertNotNull(sizes);
    assertTrue(sizes.size() >= 3);
  }

  @Test
  void shouldUpdateProductSize() {
    testSize.setSize("XXL");
    ProductSize updatedSize = productSizeRepository.save(testSize);

    assertEquals("XXL", updatedSize.getSize());
    assertEquals(testSize.getId(), updatedSize.getId());
  }

  @Test
  void shouldDeleteProductSize() {
    UUID idToDelete = testSize.getId();
    productSizeRepository.deleteById(idToDelete);

    Optional<ProductSize> deletedSize = productSizeRepository.findById(idToDelete);

    assertFalse(deletedSize.isPresent());
  }

  @Test
  void shouldReturnTrueWhenProductSizeExists() {
    assertTrue(productSizeRepository.existsById(testSize.getId()));
  }

  @Test
  void shouldReturnFalseWhenProductSizeDoesNotExist() {
    UUID randomId = UUID.randomUUID();
    assertFalse(productSizeRepository.existsById(randomId));
  }
}
