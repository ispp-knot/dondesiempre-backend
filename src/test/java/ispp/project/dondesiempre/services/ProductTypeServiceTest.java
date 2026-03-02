package ispp.project.dondesiempre.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import ispp.project.dondesiempre.models.products.ProductType;
import ispp.project.dondesiempre.repositories.products.ProductTypeRepository;
import ispp.project.dondesiempre.services.products.ProductTypeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Transactional
public class ProductTypeServiceTest {

  @Autowired private ProductTypeService productTypeService;
  @Autowired private ProductTypeRepository productTypeRepository;

  @Test
  public void shouldNotBeNull() {
    ProductType type = new ProductType();
    type.setType("Test Type");
    ProductType savedType = productTypeRepository.save(type);

    assertNotNull(savedType);
    assertEquals(
        savedType.getId(), productTypeService.getProductTypeById(savedType.getId()).getId());
  }
}
