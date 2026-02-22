package ispp.project.dondesiempre.services;

import ispp.project.dondesiempre.models.products.Product;
import ispp.project.dondesiempre.models.products.dto.ProductCreationDTO;
import ispp.project.dondesiempre.repositories.products.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

  private final ProductRepository productRepository;
  private final TypeService typeService;

  @Autowired
  public ProductService(ProductRepository productRepository, TypeService typeService) {
    this.productRepository = productRepository;
    this.typeService = typeService;
  }

  public Product saveProduct(ProductCreationDTO dto) {
    Product product = new Product();
    product.setName(dto.getName());
    product.setPrice(dto.getPrice());
    product.setDiscount(dto.getDiscount());
    product.setDescription(dto.getDescription());
    product.setType(typeService.getTypeById(dto.getTypeId()));
    return productRepository.save(product);
  }

  public Product getProductById(Integer id) {
    return productRepository.findById(id).orElse(null);
  }

  public Double getRealPrice(Product product) {
    return product.getPrice() * (1 - product.getDiscount());
  }
}
