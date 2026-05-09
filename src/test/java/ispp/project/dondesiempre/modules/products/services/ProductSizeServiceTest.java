package ispp.project.dondesiempre.modules.products.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import ispp.project.dondesiempre.modules.auth.services.UserService;
import ispp.project.dondesiempre.modules.common.exceptions.ResourceNotFoundException;
import ispp.project.dondesiempre.modules.common.exceptions.UnauthorizedException;
import ispp.project.dondesiempre.modules.products.dtos.ProductSizeCreationDTO;
import ispp.project.dondesiempre.modules.products.models.ProductSize;
import ispp.project.dondesiempre.modules.products.repositories.ProductSizeRepository;
import ispp.project.dondesiempre.modules.stores.models.Store;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Transactional
public class ProductSizeServiceTest {

  @Autowired private ProductSizeService productSizeService;
  @Autowired private ProductSizeRepository productSizeRepository;
  @MockitoBean private UserService userService;

  private ProductSize testSize;

  @BeforeEach
  void setUp() {
    testSize = new ProductSize();
    testSize.setSize("M");
    testSize = productSizeRepository.save(testSize);
  }

  @Test
  void shouldGetAllProductSizes() {
    ProductSize size2 = new ProductSize();
    size2.setSize("L");
    productSizeRepository.save(size2);

    List<ProductSize> sizes = productSizeService.getAllProductSizes();

    assertNotNull(sizes);
    assertTrue(sizes.size() >= 2);
  }

  @Test
  void shouldGetProductSizeById() {
    ProductSize foundSize = productSizeService.getProductSizeById(testSize.getId());

    assertNotNull(foundSize);
    assertEquals(testSize.getId(), foundSize.getId());
    assertEquals("M", foundSize.getSize());
  }

  @Test
  void shouldThrowExceptionWhenProductSizeNotFoundById() {
    UUID randomId = UUID.randomUUID();

    RuntimeException exception =
        assertThrows(
            RuntimeException.class,
            () -> {
              productSizeService.getProductSizeById(randomId);
            });

    assertTrue(exception.getMessage().contains("ProductSize not found"));
  }

  @Test
  void shouldReturnCorrectSizeData() {
    List<ProductSize> sizes = productSizeService.getAllProductSizes();
    ProductSize retrievedSize =
        sizes.stream().filter(s -> s.getId().equals(testSize.getId())).findFirst().orElse(null);

    assertNotNull(retrievedSize);
    assertEquals("M", retrievedSize.getSize());
  }

  @Test
  void shouldHandleMultipleProductSizes() {
    String[] sizeValues = {"XS", "S", "L", "XL", "XXL"};
    for (String size : sizeValues) {
      ProductSize productSize = new ProductSize();
      productSize.setSize(size);
      productSizeRepository.save(productSize);
    }

    List<ProductSize> sizes = productSizeService.getAllProductSizes();
    assertTrue(sizes.size() >= 6);
  }

  @Test
  void shouldCreateProductSize() {
    Store store = new Store();
    when(userService.getCurrentStore()).thenReturn(store);
    ProductSizeCreationDTO dto = new ProductSizeCreationDTO();
    dto.setSize("XS");

    ProductSize created = productSizeService.createProductSize(dto);

    assertNotNull(created);
    assertNotNull(created.getId());
    assertEquals("XS", created.getSize());
  }

  @Test
  void shouldThrowWhenClientTriesToCreateProductSize() {
    when(userService.getCurrentStore()).thenThrow(new ResourceNotFoundException("Not found."));

    assertThrows(
        UnauthorizedException.class,
        () -> productSizeService.createProductSize(new ProductSizeCreationDTO()));
  }
}
