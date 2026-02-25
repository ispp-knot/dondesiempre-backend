package ispp.project.dondesiempre.controllers;

import ispp.project.dondesiempre.dto.category.CategoryCreationDTO;
import ispp.project.dondesiempre.dto.category.CategoryResponseDTO;
import ispp.project.dondesiempre.dto.category.CategoryUpdateDTO;
import ispp.project.dondesiempre.services.CategoryService;
import jakarta.validation.Valid;
import java.util.List;
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
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CategoryController {

  private final CategoryService categoryService;

  @GetMapping("/store/{storeId}/categories")
  public ResponseEntity<List<CategoryResponseDTO>> getByStore(@PathVariable Integer storeId) {
    return ResponseEntity.ok(categoryService.getByStore(storeId));
  }

  @GetMapping("/categories/{id}")
  public ResponseEntity<CategoryResponseDTO> getById(@PathVariable Integer id) {
    return ResponseEntity.ok(categoryService.getById(id));
  }

  @PostMapping("/store/{storeId}/categories")
  public ResponseEntity<CategoryResponseDTO> create(
      @PathVariable Integer storeId, @Valid @RequestBody CategoryCreationDTO dto) {
    return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.create(storeId, dto));
  }

  @PutMapping("/categories/{id}")
  public ResponseEntity<CategoryResponseDTO> update(
      @PathVariable Integer id, @Valid @RequestBody CategoryUpdateDTO dto) {
    return ResponseEntity.ok(categoryService.update(id, dto));
  }

  @DeleteMapping("/categories/{id}")
  public ResponseEntity<Void> delete(@PathVariable Integer id) {
    categoryService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
