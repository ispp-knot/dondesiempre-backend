package ispp.project.dondesiempre.modules.products.repositories;

import ispp.project.dondesiempre.modules.products.models.ProductType;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductTypeRepository extends JpaRepository<ProductType, UUID> {}
