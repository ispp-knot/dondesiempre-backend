package ispp.project.dondesiempre.repositories.products;

import ispp.project.dondesiempre.models.products.ProductVariant;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, UUID> {}
