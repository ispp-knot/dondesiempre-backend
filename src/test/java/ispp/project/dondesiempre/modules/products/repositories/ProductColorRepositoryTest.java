package ispp.project.dondesiempre.modules.products.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ispp.project.dondesiempre.modules.products.models.ProductColor;
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
public class ProductColorRepositoryTest {

  @Autowired private ProductColorRepository productColorRepository;

  private ProductColor testColor;

  @BeforeEach
  void setUp() {
    testColor = new ProductColor();
    testColor.setColor("Red");
    testColor = productColorRepository.save(testColor);
  }

  @Test
  void shouldSaveProductColor() {
    ProductColor color = new ProductColor();
    color.setColor("Blue");
    ProductColor savedColor = productColorRepository.save(color);

    assertNotNull(savedColor);
    assertNotNull(savedColor.getId());
    assertEquals("Blue", savedColor.getColor());
  }

  @Test
  void shouldFindProductColorById() {
    Optional<ProductColor> foundColor = productColorRepository.findById(testColor.getId());

    assertTrue(foundColor.isPresent());
    assertEquals(testColor.getId(), foundColor.get().getId());
    assertEquals("Red", foundColor.get().getColor());
  }

  @Test
  void shouldReturnEmptyWhenProductColorNotFound() {
    UUID randomId = UUID.randomUUID();
    Optional<ProductColor> foundColor = productColorRepository.findById(randomId);

    assertFalse(foundColor.isPresent());
  }

  @Test
  void shouldFindAllProductColors() {
    ProductColor color2 = new ProductColor();
    color2.setColor("Green");
    productColorRepository.save(color2);

    ProductColor color3 = new ProductColor();
    color3.setColor("Yellow");
    productColorRepository.save(color3);

    List<ProductColor> colors = productColorRepository.findAll();

    assertNotNull(colors);
    assertTrue(colors.size() >= 3);
  }

  @Test
  void shouldUpdateProductColor() {
    testColor.setColor("Purple");
    ProductColor updatedColor = productColorRepository.save(testColor);

    assertEquals("Purple", updatedColor.getColor());
    assertEquals(testColor.getId(), updatedColor.getId());
  }

  @Test
  void shouldDeleteProductColor() {
    UUID idToDelete = testColor.getId();
    productColorRepository.deleteById(idToDelete);

    Optional<ProductColor> deletedColor = productColorRepository.findById(idToDelete);

    assertFalse(deletedColor.isPresent());
  }

  @Test
  void shouldReturnTrueWhenProductColorExists() {
    assertTrue(productColorRepository.existsById(testColor.getId()));
  }

  @Test
  void shouldReturnFalseWhenProductColorDoesNotExist() {
    UUID randomId = UUID.randomUUID();
    assertFalse(productColorRepository.existsById(randomId));
  }
}
