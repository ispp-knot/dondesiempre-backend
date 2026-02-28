package ispp.project.dondesiempre.repositories.outfits;

import ispp.project.dondesiempre.models.outfits.Outfit;
import ispp.project.dondesiempre.models.outfits.OutfitProduct;
import ispp.project.dondesiempre.models.outfits.OutfitTagRelation;
import ispp.project.dondesiempre.models.products.Product;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OutfitRepository extends JpaRepository<Outfit, UUID> {
  @Query(
      "select distinct op.outfit from OutfitProduct op where op.product.store.id = :storeId order by op.outfit.index asc")
  public List<Outfit> findByStoreId(UUID storeId);

  @Query("select op.product from OutfitProduct op where op.outfit.id = :id order by op.index asc")
  public List<Product> findOutfitProductsById(UUID id);

  @Query("select op.index from OutfitProduct op where op.outfit.id = :id order by op.index asc")
  public List<Integer> findOutfitProductIndicesById(UUID id);

  @Query("select op from OutfitProduct op where op.outfit.id = :id order by op.index asc")
  public List<OutfitProduct> findOutfitOutfitProductsById(UUID id);

  public List<String> findOutfitTagsById(UUID id);

  @Query("select o from Outfit o where o.storefront.id = :storefrontId")
  public List<Outfit> findByStorefrontId(Integer storefrontId);

  @Query("select ot from OutfitTagRelation ot where ot.outfit.id = :id and ot.tag.id = :tagId")
  public Optional<OutfitTagRelation> findTagRelation(Integer id, Integer tagId);

  @Query("select op from OutfitProduct op where op.outfit.id = :id and op.product.id = :productId")
  public Optional<OutfitProduct> findProductRelation(Integer id, Integer productId);
}
