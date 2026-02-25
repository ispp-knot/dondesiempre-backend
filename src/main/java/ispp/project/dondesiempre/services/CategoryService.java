package ispp.project.dondesiempre.services;

import ispp.project.dondesiempre.dto.category.CategoryCreationDTO;
import ispp.project.dondesiempre.dto.category.CategoryResponseDTO;
import ispp.project.dondesiempre.dto.category.CategoryUpdateDTO;
import ispp.project.dondesiempre.models.products.ProductCategory;
import ispp.project.dondesiempre.models.stores.Store;
import ispp.project.dondesiempre.repositories.products.CategoryRepository;
import ispp.project.dondesiempre.repositories.stores.StoreRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class CategoryService {

  private final CategoryRepository categoryRepository;
  private final StoreRepository storeRepository;

  public List<CategoryResponseDTO> getByStore(Integer storeId) {
    return categoryRepository.findByStoreId(storeId).stream()
        .map(CategoryResponseDTO::new)
        .toList();
  }

  public CategoryResponseDTO getById(Integer id) {
    ProductCategory category =
        categoryRepository
            .findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    return new CategoryResponseDTO(category);
  }

  public CategoryResponseDTO create(Integer storeId, CategoryCreationDTO dto) {
    Store store =
        storeRepository
            .findById(storeId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

    if (categoryRepository.existsByNameAndStoreId(dto.getName(), storeId)) {
      throw new ResponseStatusException(HttpStatus.CONFLICT);
    }

    ProductCategory category = dtoToEntity(dto);
    category.setStore(store);
    return new CategoryResponseDTO(categoryRepository.save(category));
  }

  public CategoryResponseDTO update(Integer id, CategoryUpdateDTO dto) {
    ProductCategory category =
        categoryRepository
            .findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

    category.setName(dto.getName());
    category.setDescription(dto.getDescription());

    return new CategoryResponseDTO(categoryRepository.save(category));
  }

  public void delete(Integer id) {
    ProductCategory category =
        categoryRepository
            .findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    categoryRepository.delete(category);
  }

  private ProductCategory dtoToEntity(CategoryCreationDTO dto) {
    ProductCategory category = new ProductCategory();
    category.setName(dto.getName());
    category.setDescription(dto.getDescription());
    return category;
  }
}
