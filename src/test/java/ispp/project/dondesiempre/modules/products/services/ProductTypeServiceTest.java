package ispp.project.dondesiempre.modules.products.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ispp.project.dondesiempre.modules.common.exceptions.ResourceNotFoundException;
import ispp.project.dondesiempre.modules.products.models.ProductType;
import ispp.project.dondesiempre.modules.products.repositories.ProductTypeRepository;
import java.util.List;
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

  @Test
  public void shouldFindAll() {
    ProductType type1 = new ProductType();
    type1.setType("Test Type 1");
    ProductType type2 = new ProductType();
    type2.setType("Test Type 2");
    productTypeRepository.save(type1);
    productTypeRepository.save(type2);
    List<ProductType> productTypes = productTypeService.findAll();
    assertNotNull(productTypes);
    assertTrue(productTypes.size() > 0);
  }

  @Test
  public void shouldSaveProductType() {
    ProductType productType = new ProductType();
    productType.setType("New Product Type");
    ProductType savedProductType = productTypeService.save(productType);
    assertNotNull(savedProductType);
    assertNotNull(savedProductType.getId());
    assertEquals("New Product Type", savedProductType.getType());
  }

  @Test
  public void shouldDeleteProductType() {
    ProductType productType = new ProductType();
    productType.setType("Product Type to Delete");
    ProductType savedProductType = productTypeRepository.save(productType);
    productTypeService.deleteById(savedProductType.getId());
    assertThrows(
        ResourceNotFoundException.class,
        () -> {
          productTypeService.getProductTypeById(savedProductType.getId());
        });
  }
}
