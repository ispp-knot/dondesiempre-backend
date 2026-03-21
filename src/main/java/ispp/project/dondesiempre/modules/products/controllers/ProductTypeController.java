package ispp.project.dondesiempre.modules.products.controllers;

import ispp.project.dondesiempre.modules.products.dtos.ProductTypeDTO;
import ispp.project.dondesiempre.modules.products.models.ProductType;
import ispp.project.dondesiempre.modules.products.services.ProductTypeService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ProductTypeController {

  private final ProductTypeService productTypeService;

  @GetMapping("/product-types")
  public ResponseEntity<List<ProductTypeDTO>> getAllProductTypes() {

    List<ProductType> productTypes = productTypeService.findAll();
    List<ProductTypeDTO> productTypeDTOs = productTypes.stream().map(ProductTypeDTO::new).toList();
    return new ResponseEntity<>(productTypeDTOs, HttpStatus.OK);
  }

  @GetMapping("/product-types/{id}")
  public ResponseEntity<ProductTypeDTO> getProductTypeById(@PathVariable UUID id) {
    ProductType productType = productTypeService.getProductTypeById(id);
    return new ResponseEntity<>(new ProductTypeDTO(productType), HttpStatus.OK);
  }
}
