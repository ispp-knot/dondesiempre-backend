package ispp.project.dondesiempre.services.products;

import ispp.project.dondesiempre.exceptions.ResourceNotFoundException;
import ispp.project.dondesiempre.models.products.Product;
import ispp.project.dondesiempre.repositories.products.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {
  private final ProductRepository productRepository;

  @Transactional(readOnly = true, rollbackFor = ResourceNotFoundException.class)
  public Product findById(Integer id) throws ResourceNotFoundException {
    return productRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Product with ID " + id + " not found."));
  }
}
