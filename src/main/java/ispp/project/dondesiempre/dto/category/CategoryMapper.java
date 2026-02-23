package ispp.project.dondesiempre.dto.category;

import ispp.project.dondesiempre.models.products.Category;

public class CategoryMapper {

  public static CategoryResponseDTO toDTO(Category category) {
    CategoryResponseDTO dto = new CategoryResponseDTO();
    dto.setId(category.getId());
    dto.setName(category.getName());
    dto.setDescription(category.getDescription());
    dto.setStoreId(category.getStore().getId());
    dto.setStoreName(category.getStore().getName());
    return dto;
  }

  public static Category toEntity(CategoryRequestDTO dto) {
    Category category = new Category();
    category.setName(dto.getName());
    category.setDescription(dto.getDescription());
    return category;
  }
}
