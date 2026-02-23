package ispp.project.dondesiempre.repositories.stores;

import ispp.project.dondesiempre.models.stores.Store;
import ispp.project.dondesiempre.models.stores.dto.StoresBoundingBoxDTO;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StoreRepository extends JpaRepository<Store, Integer> {

  @Query(
      value =
          """
        SELECT
            s.id AS id,
            s.name AS name,
            s.email AS email,
            s.storeid AS "storeID",
            s.address AS address,
            s.opening_hours AS "openingHours",
            s.phone AS phone,
            s.accepts_shipping AS "acceptsShipping",
            ST_Y(s.location) AS latitude,
            ST_X(s.location) AS longitude
        FROM stores s
        WHERE s.location && ST_MakeEnvelope(:minLon, :minLat, :maxLon, :maxLat, 4326)
        LIMIT 500
        """,
      nativeQuery = true)
  List<StoresBoundingBoxDTO> findStoresInBoundingBox(
      @Param("minLon") double minLon,
      @Param("minLat") double minLat,
      @Param("maxLon") double maxLon,
      @Param("maxLat") double maxLat);
}
