package ispp.project.dondesiempre.controllers;

import ispp.project.dondesiempre.dto.category.CategoryMapper;
import ispp.project.dondesiempre.dto.category.CategoryRequestDTO;
import ispp.project.dondesiempre.dto.category.CategoryResponseDTO;
import ispp.project.dondesiempre.models.products.Category;
import ispp.project.dondesiempre.models.stores.Store;
import ispp.project.dondesiempre.repositories.products.CategoryRepository;
import ispp.project.dondesiempre.repositories.stores.StoreRepository;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

  private final CategoryRepository categoryRepository;
  private final StoreRepository storeRepository;

  @GetMapping("/store/{storeId}")
  public ResponseEntity<List<CategoryResponseDTO>> getByStore(@PathVariable Integer storeId) {
    List<CategoryResponseDTO> categories =
        categoryRepository.findByStoreId(storeId).stream()
            .map(CategoryMapper::toDTO)
            .collect(Collectors.toList());
    return ResponseEntity.ok(categories);
  }

  @GetMapping("/{id}")
  public ResponseEntity<CategoryResponseDTO> getById(@PathVariable Integer id) {
    return categoryRepository
        .findById(id)
        .map(CategoryMapper::toDTO)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @PostMapping
  public ResponseEntity<CategoryResponseDTO> create(@Valid @RequestBody CategoryRequestDTO dto) {
    Store store = storeRepository.findById(dto.getStoreId()).orElse(null);
    if (store == null) return ResponseEntity.badRequest().build();

    if (categoryRepository.existsByNameAndStoreId(dto.getName(), dto.getStoreId())) {
      return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    Category category = CategoryMapper.toEntity(dto);
    category.setStore(store);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(CategoryMapper.toDTO(categoryRepository.save(category)));
  }

  @PutMapping("/{id}")
  public ResponseEntity<CategoryResponseDTO> update(
      @PathVariable Integer id, @Valid @RequestBody CategoryRequestDTO dto) {
    return categoryRepository
        .findById(id)
        .map(
            category -> {
              category.setName(dto.getName());
              category.setDescription(dto.getDescription());
              return ResponseEntity.ok(CategoryMapper.toDTO(categoryRepository.save(category)));
            })
        .orElse(ResponseEntity.notFound().build());
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Integer id) {
    if (!categoryRepository.existsById(id)) return ResponseEntity.notFound().build();
    categoryRepository.deleteById(id);
    return ResponseEntity.noContent().build();
  }
}
