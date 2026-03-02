package ispp.project.dondesiempre.services.collections;

import ispp.project.dondesiempre.exceptions.InvalidRequestException;
import ispp.project.dondesiempre.exceptions.RequestConflictException;
import ispp.project.dondesiempre.exceptions.ResourceNotFoundException;
import ispp.project.dondesiempre.models.collections.ProductCollection;
import ispp.project.dondesiempre.models.collections.dto.CollectionCreationDTO;
import ispp.project.dondesiempre.models.collections.dto.CollectionUpdateDTO;
import ispp.project.dondesiempre.models.products.Product;
import ispp.project.dondesiempre.models.stores.Store;
import ispp.project.dondesiempre.repositories.collections.CollectionRepository;
import ispp.project.dondesiempre.repositories.products.ProductRepository;
import ispp.project.dondesiempre.repositories.stores.StoreRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CollectionService {

  private final CollectionRepository collectionRepository;
  private final ProductRepository productRepository;
  private final StoreRepository storeRepository;

  public List<ProductCollection> getByStore(UUID storeId) {
    return collectionRepository.findByStoreId(storeId);
  }

  public ProductCollection getById(UUID id) {
    return collectionRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Collection not found"));
  }

  public ProductCollection create(UUID storeId, CollectionCreationDTO dto) {
    Store store =
        storeRepository
            .findById(storeId)
            .orElseThrow(() -> new ResourceNotFoundException("Store not found"));
    if (collectionRepository.existsByNameAndStoreId(dto.getName(), storeId)) {
      throw new RequestConflictException("A collection with this name already exists in the store");
    }
    ProductCollection collection = dtoToEntity(dto, storeId);
    collection.setStore(store);
    return collectionRepository.save(collection);
  }

  public ProductCollection update(UUID id, CollectionUpdateDTO dto) {
    ProductCollection collection =
        collectionRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Collection not found"));
    UUID storeId = collection.getStore().getId();
    if (collectionRepository.existsByNameAndStoreIdAndIdNot(dto.getName(), storeId, id)) {
      throw new RequestConflictException("Another collection with this name already exists");
    }
    collection.setName(dto.getName());
    collection.setDescription(dto.getDescription());
    collection.setProducts(new HashSet<>(fetchValidatedProducts(dto.getProductIds(), storeId)));
    return collectionRepository.save(collection);
  }

  public void delete(UUID id) {
    ProductCollection collection =
        collectionRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Collection not found"));
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
      throw new ResourceNotFoundException("Some products were not found");
    }
    boolean allBelongToStore =
        products.stream().allMatch(product -> product.getStore().getId().equals(storeId));
    if (!allBelongToStore) {
      throw new InvalidRequestException("Products must belong to the same store as the collection");
    }
    return products;
  }
}
