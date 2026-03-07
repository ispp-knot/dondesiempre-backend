package ispp.project.dondesiempre.modules.stores.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ispp.project.dondesiempre.modules.stores.models.Store;

public interface StoreRepository extends JpaRepository<Store, UUID> {

  Optional<Store> findByUserId(UUID userId);

  @Query(value = """
      SELECT
          *
      FROM stores s
      WHERE s.location && ST_MakeEnvelope(:minLon, :minLat, :maxLon, :maxLat, 4326)
      LIMIT :numResults
      """, nativeQuery = true)
  List<Store> findStoresInBoundingBox(
      @Param("minLon") double minLon,
      @Param("minLat") double minLat,
      @Param("maxLon") double maxLon,
      @Param("maxLat") double maxLat,
      @Param("numResults") int numResults);
}
