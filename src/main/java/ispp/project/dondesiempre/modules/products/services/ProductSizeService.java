package ispp.project.dondesiempre.modules.products.services;

import ispp.project.dondesiempre.modules.auth.services.AuthService;
import ispp.project.dondesiempre.modules.common.exceptions.ResourceNotFoundException;
import ispp.project.dondesiempre.modules.products.dtos.ProductSizeCreationDTO;
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
  private final AuthService authService;

  public List<ProductSize> getAllProductSizes() {
    return productSizeRepository.findAll();
  }

  public ProductSize getProductSizeById(UUID id) {
    return productSizeRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("ProductSize not found with id: " + id));
  }

  public ProductSize createProductSize(ProductSizeCreationDTO dto) {
    authService.assertUserIsStore();
    ProductSize size = new ProductSize();
    size.setSize(dto.getSize());
    return productSizeRepository.save(size);
  }
}
