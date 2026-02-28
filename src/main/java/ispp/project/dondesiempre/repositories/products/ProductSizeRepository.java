package ispp.project.dondesiempre.repositories.products;

import ispp.project.dondesiempre.models.products.ProductSize;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductSizeRepository extends JpaRepository<ProductSize, UUID> {}
