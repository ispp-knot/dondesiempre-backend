package ispp.project.dondesiempre.services.products;

import java.util.UUID;

import ispp.project.dondesiempre.exceptions.ResourceNotFoundException;
import ispp.project.dondesiempre.models.products.ProductType;
import ispp.project.dondesiempre.repositories.products.ProductTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductTypeService {

  private final ProductTypeRepository productTypeRepository;

  public ProductType getProductTypeById(UUID id) {
    return productTypeRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Product type not found"));
  }
}
