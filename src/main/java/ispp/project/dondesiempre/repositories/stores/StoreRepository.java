package ispp.project.dondesiempre.repositories.stores;

import java.util.UUID;

import ispp.project.dondesiempre.models.stores.Store;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StoreRepository extends JpaRepository<Store, UUID> {

  @Query(
      value =
          """
        SELECT
            *
        FROM stores s
        WHERE s.location && ST_MakeEnvelope(:minLon, :minLat, :maxLon, :maxLat, 4326)
        LIMIT :numResults
        """,
      nativeQuery = true)
  List<Store> findStoresInBoundingBox(
      @Param("minLon") double minLon,
      @Param("minLat") double minLat,
      @Param("maxLon") double maxLon,
      @Param("maxLat") double maxLat,
      @Param("numResults") int numResults);
}
