package ispp.project.dondesiempre.services;

import ispp.project.dondesiempre.dto.collection.CollectionCreationDTO;
import ispp.project.dondesiempre.dto.collection.CollectionResponseDTO;
import ispp.project.dondesiempre.dto.collection.CollectionUpdateDTO;
import ispp.project.dondesiempre.models.products.Product;
import ispp.project.dondesiempre.models.products.ProductCollection;
import ispp.project.dondesiempre.models.stores.Store;
import ispp.project.dondesiempre.repositories.products.CollectionRepository;
import ispp.project.dondesiempre.repositories.products.ProductRepository;
import ispp.project.dondesiempre.repositories.stores.StoreRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class CollectionService {

  private final CollectionRepository collectionRepository;
  private final ProductRepository productRepository;
  private final StoreRepository storeRepository;

  public List<CollectionResponseDTO> getByStore(UUID storeId) {
    return collectionRepository.findByStoreId(storeId).stream()
        .map(CollectionResponseDTO::new)
        .toList();
  }

  public CollectionResponseDTO getById(UUID id) {
    ProductCollection collection =
        collectionRepository
            .findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    return new CollectionResponseDTO(collection);
  }

  public CollectionResponseDTO create(UUID storeId, CollectionCreationDTO dto) {
    Store store =
        storeRepository
            .findById(storeId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

    if (collectionRepository.existsByNameAndStoreId(dto.getName(), storeId)) {
      throw new ResponseStatusException(HttpStatus.CONFLICT);
    }

    ProductCollection collection = dtoToEntity(dto, storeId);
    collection.setStore(store);

    return new CollectionResponseDTO(collectionRepository.save(collection));
  }

  public CollectionResponseDTO update(UUID id, CollectionUpdateDTO dto) {
    ProductCollection collection =
        collectionRepository
            .findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

    UUID storeId = collection.getStore().getId();
    if (collectionRepository.existsByNameAndStoreIdAndIdNot(dto.getName(), storeId, id)) {
      throw new ResponseStatusException(HttpStatus.CONFLICT);
    }

    collection.setName(dto.getName());
    collection.setDescription(dto.getDescription());
    collection.setProducts(new HashSet<>(fetchValidatedProducts(dto.getProductIds(), storeId)));

    return new CollectionResponseDTO(collectionRepository.save(collection));
  }

  public void delete(UUID id) {
    ProductCollection collection =
        collectionRepository
            .findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    collectionRepository.delete(collection);
  }

  private ProductCollection dtoToEntity(CollectionCreationDTO dto, UUID storeId) {
    ProductCollection collection = new ProductCollection();
    collection.setName(dto.getName());
    collection.setDescription(dto.getDescription());
    collection.setProducts(new HashSet<>(fetchValidatedProducts(dto.getProductIds(), storeId)));
    return collection;
  }

  private List<Product> fetchValidatedProducts(Set<UUID> productIds, UUID storeId) {
    if (productIds == null || productIds.isEmpty()) {
      return List.of();
    }

    List<Product> products = productRepository.findByIdIn(productIds);
    if (products.size() != productIds.size()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Some products were not found");
    }

    boolean allBelongToStore =
        products.stream().allMatch(product -> product.getStore().getId().equals(storeId));
    if (!allBelongToStore) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "Products must belong to the same store as the collection");
    }

    return products;
  }
}
