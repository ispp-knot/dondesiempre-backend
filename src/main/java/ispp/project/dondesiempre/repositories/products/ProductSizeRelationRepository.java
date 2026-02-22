package ispp.project.dondesiempre.repositories.products;

import ispp.project.dondesiempre.models.products.ProductSizeRelation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductSizeRelationRepository
    extends JpaRepository<ProductSizeRelation, Integer> {}
