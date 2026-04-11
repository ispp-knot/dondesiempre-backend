package ispp.project.dondesiempre.modules.products.services;

import ispp.project.dondesiempre.modules.common.exceptions.ResourceNotFoundException;
import ispp.project.dondesiempre.modules.products.models.ProductColor;
import ispp.project.dondesiempre.modules.products.repositories.ProductColorRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductColorService {

  private final ProductColorRepository productColorRepository;

  public List<ProductColor> getAllProductColors() {
    return productColorRepository.findAll();
  }

  public ProductColor getProductColorById(UUID id) {
    return productColorRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("ProductColor not found with id: " + id));
  }
}
