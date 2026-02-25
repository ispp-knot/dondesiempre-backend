package ispp.project.dondesiempre.repositories;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ispp.project.dondesiempre.models.products.Product;
import ispp.project.dondesiempre.repositories.products.ProductRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class ProductRepositoryTest {

  @Autowired private ProductRepository productRepository;

  @Test
  public void shouldOnlyReturnDiscountedProducts() {
    List<Product> discountedProducts = productRepository.findAllDiscountedProducts();

    assertNotNull(discountedProducts);
    assertFalse(discountedProducts.isEmpty());
    for (Product product : discountedProducts) {
      assertTrue(product.getDiscountedPriceInCents() < product.getPriceInCents());
    }
  }
}
