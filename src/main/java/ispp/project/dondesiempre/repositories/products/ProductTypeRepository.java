package ispp.project.dondesiempre.repositories.products;

import java.util.UUID;

import ispp.project.dondesiempre.models.products.ProductType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductTypeRepository extends JpaRepository<ProductType, UUID> {}
