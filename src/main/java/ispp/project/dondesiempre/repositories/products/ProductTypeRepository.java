package ispp.project.dondesiempre.repositories.products;

import ispp.project.dondesiempre.models.products.ProductType;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductTypeRepository extends JpaRepository<ProductType, UUID> {}
