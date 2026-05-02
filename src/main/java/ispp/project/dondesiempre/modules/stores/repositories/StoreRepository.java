package ispp.project.dondesiempre.modules.stores.repositories;

import ispp.project.dondesiempre.modules.stores.models.Store;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StoreRepository extends JpaRepository<Store, UUID> {

  Optional<Store> findByUserId(UUID userId);

  @Query(
      value =
          """
            SELECT
                *
            FROM stores s
            WHERE (:name IS NULL OR s.name ILIKE CONCAT('%', :name, '%') ESCAPE '\\')
            ORDER BY
                CASE WHEN :lat IS NOT NULL AND :lon IS NOT NULL
                THEN ST_Distance(s.location, ST_SetSRID(ST_MakePoint(:lon, :lat), 4326))
                ELSE 0 END ASC,
                s.name ASC
            LIMIT :numResults
            """,
      nativeQuery = true)
  List<Store> searchStores(
      @Param("name") String name,
      @Param("lat") Double lat,
      @Param("lon") Double lon,
      @Param("numResults") int numResults);

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

  boolean existsByIdAndPremiumPlanTrue(UUID id);
}
