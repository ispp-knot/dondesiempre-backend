package ispp.project.dondesiempre.modules.products.repositories;

import ispp.project.dondesiempre.modules.products.models.ProductVariant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, UUID> {

  List<ProductVariant> findByProductId(UUID productId);

  List<ProductVariant> findByProductIdAndIsAvailableTrue(UUID productId);

  List<ProductVariant> findBySizeId(UUID sizeId);

  List<ProductVariant> findByColorId(UUID colorId);
}
