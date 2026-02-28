package ispp.project.dondesiempre.repositories.products;

import java.util.UUID;

import ispp.project.dondesiempre.models.products.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, UUID> {}
