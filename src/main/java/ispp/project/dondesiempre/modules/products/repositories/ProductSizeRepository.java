package ispp.project.dondesiempre.modules.products.repositories;

import ispp.project.dondesiempre.modules.products.models.ProductSize;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductSizeRepository extends JpaRepository<ProductSize, UUID> {}
