package ispp.project.dondesiempre.services;

import ispp.project.dondesiempre.exceptions.ResourceNotFoundException;
import ispp.project.dondesiempre.models.products.Product;
import ispp.project.dondesiempre.repositories.products.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {
  private final ProductRepository productRepository;

  @Autowired
  public ProductService(ProductRepository productRepository) {
    this.productRepository = productRepository;
  }

  @Transactional(readOnly = true)
  public Product findById(Integer id) throws ResourceNotFoundException {
    return productRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Product with ID " + id + " not found."));
  }
}
