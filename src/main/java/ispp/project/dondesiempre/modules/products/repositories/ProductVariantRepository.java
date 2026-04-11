package ispp.project.dondesiempre.modules.products.repositories;

import ispp.project.dondesiempre.modules.products.models.ProductVariant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, UUID> {

  List<ProductVariant> findByProductId(UUID productId);

  List<ProductVariant> findByProductIdAndIsDeletedIsFalse(UUID productId);

  List<ProductVariant> findByProductIdAndIsAvailableTrue(UUID productId);

  List<ProductVariant> findByProductIdAndIsAvailableTrueAndIsDeletedIsFalse(UUID productId);

  List<ProductVariant> findByIsDeletedIsFalse();

  Optional<ProductVariant> findByIdAndIsDeletedIsFalse(UUID id);

  List<ProductVariant> findBySizeId(UUID sizeId);

  List<ProductVariant> findByColorId(UUID colorId);

  boolean existsByProductIdAndSizeIdAndColorId(UUID productId, UUID sizeId, UUID colorId);
}
