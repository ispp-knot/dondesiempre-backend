package ispp.project.dondesiempre.repositories.products;

import java.util.UUID;

import ispp.project.dondesiempre.models.products.ProductSize;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductSizeRepository extends JpaRepository<ProductSize, UUID> {}
