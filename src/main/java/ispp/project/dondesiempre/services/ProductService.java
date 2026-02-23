package ispp.project.dondesiempre.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ispp.project.dondesiempre.exceptions.ResourceNotFoundException;
import ispp.project.dondesiempre.models.products.Product;
import ispp.project.dondesiempre.repositories.products.ProductRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {
  private final ProductRepository productRepository;

  @Transactional(readOnly = true)
  public Product findById(Integer id) throws ResourceNotFoundException {
    return productRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Product with ID " + id + " not found."));
  }
}
