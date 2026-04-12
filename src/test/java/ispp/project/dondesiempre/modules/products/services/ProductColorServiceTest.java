package ispp.project.dondesiempre.modules.products.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ispp.project.dondesiempre.modules.products.models.ProductColor;
import ispp.project.dondesiempre.modules.products.repositories.ProductColorRepository;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Transactional
public class ProductColorServiceTest {

  @Autowired private ProductColorService productColorService;
  @Autowired private ProductColorRepository productColorRepository;

  private ProductColor testColor;

  @BeforeEach
  void setUp() {
    testColor = new ProductColor();
    testColor.setColor("Red");
    testColor = productColorRepository.save(testColor);
  }

  @Test
  void shouldGetAllProductColors() {
    ProductColor color2 = new ProductColor();
    color2.setColor("Blue");
    productColorRepository.save(color2);

    List<ProductColor> colors = productColorService.getAllProductColors();

    assertNotNull(colors);
    assertTrue(colors.size() >= 2);
  }

  @Test
  void shouldGetProductColorById() {
    ProductColor foundColor = productColorService.getProductColorById(testColor.getId());

    assertNotNull(foundColor);
    assertEquals(testColor.getId(), foundColor.getId());
    assertEquals("Red", foundColor.getColor());
  }

  @Test
  void shouldThrowExceptionWhenProductColorNotFoundById() {
    UUID randomId = UUID.randomUUID();

    RuntimeException exception =
        assertThrows(
            RuntimeException.class,
            () -> {
              productColorService.getProductColorById(randomId);
            });

    assertTrue(exception.getMessage().contains("ProductColor not found"));
  }

  @Test
  void shouldReturnCorrectColorData() {
    List<ProductColor> colors = productColorService.getAllProductColors();
    ProductColor retrievedColor =
        colors.stream().filter(c -> c.getId().equals(testColor.getId())).findFirst().orElse(null);

    assertNotNull(retrievedColor);
    assertEquals("Red", retrievedColor.getColor());
  }

  @Test
  void shouldHandleMultipleProductColors() {
    for (int i = 0; i < 5; i++) {
      ProductColor color = new ProductColor();
      color.setColor("Color" + i);
      productColorRepository.save(color);
    }

    List<ProductColor> colors = productColorService.getAllProductColors();
    assertTrue(colors.size() >= 6);
  }
}
