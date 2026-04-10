package ispp.project.dondesiempre.modules.products.services;

import ispp.project.dondesiempre.modules.products.models.ProductSize;
import ispp.project.dondesiempre.modules.products.repositories.ProductSizeRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductSizeService {

  private final ProductSizeRepository productSizeRepository;

  public List<ProductSize> getAllProductSizes() {
    return productSizeRepository.findAll();
  }

  public ProductSize getProductSizeById(UUID id) {
    return productSizeRepository
        .findById(id)
        .orElseThrow(() -> new RuntimeException("ProductSize not found with id: " + id));
  }
}
