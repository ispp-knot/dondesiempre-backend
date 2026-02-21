package ispp.project.dondesiempre.repositories.products;

import ispp.project.dondesiempre.models.products.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Integer> {}
