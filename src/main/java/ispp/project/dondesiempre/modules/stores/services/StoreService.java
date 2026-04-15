package ispp.project.dondesiempre.modules.stores.services;

import ispp.project.dondesiempre.modules.auth.services.AuthService;
import ispp.project.dondesiempre.modules.common.exceptions.InvalidBoundingBoxException;
import ispp.project.dondesiempre.modules.common.exceptions.ResourceNotFoundException;
import ispp.project.dondesiempre.modules.common.exceptions.UnauthorizedException;
import ispp.project.dondesiempre.modules.promotions.repositories.PromotionRepository;
import ispp.project.dondesiempre.modules.stores.dtos.StoreDTO;
import ispp.project.dondesiempre.modules.stores.dtos.StoreSocialNetworkDTO;
import ispp.project.dondesiempre.modules.stores.dtos.StoreUpdateDTO;
import ispp.project.dondesiempre.modules.stores.models.Store;
import ispp.project.dondesiempre.modules.stores.repositories.StoreRepository;
import ispp.project.dondesiempre.modules.stores.repositories.StoreSocialNetworkRepository;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StoreService {
  private final StoreRepository storeRepository;
  private final StoreSocialNetworkRepository storeSocialNetworkRepository;
  private final PromotionRepository promotionRepository;
  private final ApplicationContext applicationContext;
  private final AuthService authService;

  @Transactional(readOnly = true, rollbackFor = ResourceNotFoundException.class)
  public Store findById(UUID id) throws ResourceNotFoundException {
    return storeRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Store with ID " + id + " not found."));
  }

  @Transactional(readOnly = true)
  public StoreDTO toDTO(Store store) {
    return toDTO(store, null, null);
  }

  @Transactional(readOnly = true)
  public StoreDTO toDTO(Store store, Double userLat, Double userLon) {
    StoreDTO dto = new StoreDTO(store);
    dto.setSocialNetworks(
        storeSocialNetworkRepository.findByStoreId(store.getId()).stream()
            .map(StoreSocialNetworkDTO::new)
            .toList());
    dto.setHasActivePromotions(promotionRepository.existsByStoreIdAndIsActiveTrue(store.getId()));
    return dto;
  }

  @Transactional(readOnly = true)
  public List<Store> getStores(
      Double minLon,
      Double minLat,
      Double maxLon,
      Double maxLat,
      String name,
      Double lat,
      Double lon) {
    if (minLon != null && minLat != null && maxLon != null && maxLat != null) {
      return findStoresInBoundingBox(minLon, minLat, maxLon, maxLat);
    }
    return searchStores(name, lat, lon);
  }

  @Transactional(readOnly = true)
  public List<Store> searchStores(String name, Double lat, Double lon) {
    return storeRepository.searchStores(name, lat, lon, 100);
  }

  @Transactional(readOnly = true, rollbackFor = InvalidBoundingBoxException.class)
  public List<Store> findStoresInBoundingBox(
      Double minLon, Double minLat, Double maxLon, Double maxLat)
      throws InvalidBoundingBoxException {
    if (minLon > maxLon || minLat > maxLat)
      throw new InvalidBoundingBoxException(
          "Invalid bounding box parameters: minimum coordinates cannot be greater than maximum coordinates.");
    return storeRepository.findStoresInBoundingBox(minLon, minLat, maxLon, maxLat, 500);
  }

  @Transactional(readOnly = true, rollbackFor = InvalidBoundingBoxException.class)
  public List<StoreDTO> findStoresInBoundingBoxAsDTO(
      double minLon, double minLat, double maxLon, double maxLat)
      throws InvalidBoundingBoxException {
    if (minLon > maxLon || minLat > maxLat)
      throw new InvalidBoundingBoxException(
          "Invalid bounding box parameters: minimum coordinates cannot be greater than maximum coordinates.");
    List<Store> stores =
        storeRepository.findStoresInBoundingBox(minLon, minLat, maxLon, maxLat, 500);
    if (stores.isEmpty()) return List.of();

    List<UUID> ids = stores.stream().map(Store::getId).toList();
    Map<UUID, List<StoreSocialNetworkDTO>> socialNetworksByStore =
        storeSocialNetworkRepository.findByStoreIdsWithSocialNetwork(ids).stream()
            .collect(
                Collectors.groupingBy(
                    s -> s.getStore().getId(),
                    Collectors.mapping(StoreSocialNetworkDTO::new, Collectors.toList())));

    return stores.stream()
        .map(
            store -> {
              StoreDTO dto = new StoreDTO(store);
              dto.setSocialNetworks(socialNetworksByStore.getOrDefault(store.getId(), List.of()));
              dto.setHasActivePromotions(
                  promotionRepository.existsByStoreIdAndIsActiveTrue(store.getId()));
              return dto;
            })
        .toList();
  }

  public List<StoreDTO> findAll() {
    List<Store> stores = storeRepository.findAll();
    return stores.stream().map(StoreDTO::new).toList();
  }

  @Transactional(rollbackFor = {UnauthorizedException.class, ResourceNotFoundException.class})
  public StoreDTO updateStore(UUID id, StoreUpdateDTO dto)
      throws UnauthorizedException, ResourceNotFoundException {
    Store storeToUpdate;

    storeToUpdate = applicationContext.getBean(StoreService.class).findById(id);
    authService.assertUserOwnsStore(storeToUpdate);

    if (dto.getName() != null) storeToUpdate.setName(dto.getName());
    if (dto.getEmail() != null) storeToUpdate.setEmail(dto.getEmail());
    if (dto.getAddress() != null) storeToUpdate.setAddress(dto.getAddress());
    if (dto.getOpeningHours() != null) storeToUpdate.setOpeningHours(dto.getOpeningHours());
    storeToUpdate.setAboutUs(dto.getAboutUs());

    return applicationContext
        .getBean(StoreService.class)
        .toDTO(storeRepository.save(storeToUpdate));
  }

  @Transactional
  public StoreDTO updateLocation(UUID id, Double longitude, Double latitude) {
    Store store = applicationContext.getBean(StoreService.class).findById(id);
    authService.assertUserOwnsStore(store);

    Coordinate coordinate = new Coordinate(longitude, latitude);
    GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
    Point newLocation = geometryFactory.createPoint(coordinate);

    store.setLocation(newLocation);
    return applicationContext.getBean(StoreService.class).toDTO(storeRepository.save(store));
  }

  @Transactional(rollbackFor = {UnauthorizedException.class})
  public Store setAccountId(UUID storeId, String accountId) {
    Store store = applicationContext.getBean(StoreService.class).findById(storeId);

    store.setAccountId(accountId);

    return store;
  }

  @Transactional(readOnly = true)
  public boolean checkStoreIsPremium(UUID storeId) {
    return storeRepository.existsByIdAndPremiumPlanTrue(storeId);
  }
}
