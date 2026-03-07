package ispp.project.dondesiempre.modules.products.repositories;

import ispp.project.dondesiempre.modules.products.models.ProductColor;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductColorRepository extends JpaRepository<ProductColor, UUID> {}
