package ispp.project.dondesiempre.modules.products.services;

import ispp.project.dondesiempre.modules.common.exceptions.ResourceNotFoundException;
import ispp.project.dondesiempre.modules.products.models.ProductType;
import ispp.project.dondesiempre.modules.products.repositories.ProductTypeRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductTypeService {

  private final ProductTypeRepository productTypeRepository;

  @Transactional(
      readOnly = true,
      rollbackFor = {ResourceNotFoundException.class})
  public ProductType getProductTypeById(UUID id) throws ResourceNotFoundException {
    return productTypeRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Product type not found"));
  }
}
