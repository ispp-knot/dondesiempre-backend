package ispp.project.dondesiempre.repositories.products;

import org.springframework.data.jpa.repository.JpaRepository;

import ispp.project.dondesiempre.models.products.Product;

public interface ProductRepository extends JpaRepository<Product, Integer> {

}
