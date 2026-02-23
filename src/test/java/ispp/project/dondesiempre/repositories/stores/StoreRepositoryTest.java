package ispp.project.dondesiempre.repositories.stores;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import ispp.project.dondesiempre.models.storefronts.Storefront;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import ispp.project.dondesiempre.models.stores.Store;
import ispp.project.dondesiempre.models.stores.dto.StoresBoundingBoxDTO;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class StoreRepositoryTest {
    
    @Autowired private StoreRepository storeRepository;

    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    @BeforeEach
    void setUp() {
        // Limpiamos la base de datos antes de cada test para asegurar resultados predecibles
        storeRepository.deleteAll();
    }

    // ==========================================
    // MÉTODOS AUXILIARES
    // ==========================================

    private Point createPoint(double longitude, double latitude) {
        return geometryFactory.createPoint(new Coordinate(longitude, latitude));
    }

    private void createTestStore(String name, double longitude, double latitude) {
        Store store = new Store();
        store.setName(name);
        store.setEmail("test@test.com");
        store.setStoreID("ST-001");
        store.setAddress("Direccion de prueba");
        store.setOpeningHours("09:00-18:00");
        store.setPhone("123456789");
        store.setAcceptsShipping(true);
        store.setLocation(createPoint(longitude, latitude));

        Storefront storefront = new Storefront();
        store.setStorefront(storefront);

        storeRepository.save(store);
    }

    // ==========================================
    // TESTS POSITIVOS
    // ==========================================

    @Test
    void shouldReturnStore_whenStoreIsInsideBoundingBox() {
        // Dado: Una tienda en Dos Hermanas (Longitud: -5.93, Latitud: 37.29)
        createTestStore("Tienda Dos Hermanas", -5.932650, 37.290025);

        // Cuando: Buscamos en un cuadrado amplio que la contiene
        List<StoresBoundingBoxDTO> result = storeRepository.findStoresInBoundingBox(
                -6.000000, 37.000000, // minLon, minLat (Suroeste)
                -5.800000, 37.500000  // maxLon, maxLat (Noreste)
        );

        // Entonces: Debería encontrarla
        assertEquals(1, result.size());
        assertEquals("Tienda Dos Hermanas", result.getFirst().getName());
    }

    @Test
    void shouldMapProjectionAliasesCorrectly() {
        // Dado: Una tienda guardada
        createTestStore("Tienda Mapeo", -5.932650, 37.290025);

        // Cuando: Recuperamos la tienda
        List<StoresBoundingBoxDTO> result = storeRepository.findStoresInBoundingBox(-6.0, 37.0, -5.8, 37.5);

        // Entonces: Verificamos que el mapeo nativo de PostgreSQL con comillas dobles funcionó
        assertFalse(result.isEmpty());
        StoresBoundingBoxDTO dto = result.getFirst();

        assertNotNull(dto.getId());
        assertEquals("Tienda Mapeo", dto.getName());
        assertEquals("ST-001", dto.getStoreID());
        assertEquals("09:00-18:00", dto.getOpeningHours());
        assertTrue(dto.getAcceptsShipping());
        assertEquals(37.290025, dto.getLatitude());
        assertEquals(-5.932650, dto.getLongitude());
    }

    @Test
    void shouldReturnStore_whenStoreIsExactlyOnTheBoundary() {
        // Dado: Una tienda
        createTestStore("Tienda Límite", -5.90, 37.30);

        // Cuando: Buscamos donde uno de los bordes del cuadrado toca exactamente la tienda
        List<StoresBoundingBoxDTO> result = storeRepository.findStoresInBoundingBox(
                -5.90, 37.00, // minLon coincide exactamente con la tienda
                -5.00, 38.00
        );

        // Entonces: ST_MakeEnvelope es inclusivo (>= y <=), por lo que debe encontrarla
        assertEquals(1, result.size());
    }

    @Test
    void shouldReturnMultipleStores_whenMultipleInsideBoundingBox() {
        // Dado: Varias tiendas
        createTestStore("Tienda 1", -5.91, 37.21);
        createTestStore("Tienda 2", -5.92, 37.22);
        createTestStore("Tienda Fuera", 2.17, 41.38); // Esta está en Barcelona, fuera de la caja

        // Cuando
        List<StoresBoundingBoxDTO> result = storeRepository.findStoresInBoundingBox(-6.0, 37.0, -5.0, 38.0);

        // Entonces: Solo trae las 2 de la zona
        assertEquals(2, result.size());
    }

    // ==========================================
    // TESTS NEGATIVOS
    // ==========================================

    @Test
    void shouldReturnNoStores_whenNoStoresInDatabase() {
        List<StoresBoundingBoxDTO> result = storeRepository.findStoresInBoundingBox(0, 0, 0, 0);
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnNoStores_whenStoreIsOutsideBoundingBox() {
        // Dado: Una tienda en Dos Hermanas
        createTestStore("Tienda Dos Hermanas", -5.932650, 37.290025);

        // Cuando: Buscamos en un cuadrado en Madrid (Long: -3.7, Lat: 40.4)
        List<StoresBoundingBoxDTO> result = storeRepository.findStoresInBoundingBox(
                -4.00, 40.00,
                -3.00, 41.00
        );

        // Entonces: No debe encontrar nada
        assertTrue(result.isEmpty());
    }
}
