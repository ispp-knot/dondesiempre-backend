package ispp.project.dondesiempre.services.products;

import ispp.project.dondesiempre.models.products.ProductType;
import ispp.project.dondesiempre.repositories.products.ProductTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductTypeService {

  private final ProductTypeRepository productTypeRepository;

  public ProductType getProductTypeById(Integer id) {
    return productTypeRepository
        .findById(id)
        .orElseThrow(() -> new RuntimeException("Product type not found"));
  }
}
