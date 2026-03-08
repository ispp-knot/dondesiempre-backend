package ispp.project.dondesiempre.modules.promotions.dtos;

import ispp.project.dondesiempre.modules.promotions.models.Promotion;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PromotionDTO {

  private UUID id;
  private String name;
  private Integer discountPercentage;
  private boolean isActive;
  private String description;
  private UUID storeId;
  private List<UUID> productIds;

  public PromotionDTO(Promotion promotion, List<UUID> productIds) {
    this.setId(promotion.getId());
    this.setName(promotion.getName());
    this.setDiscountPercentage(promotion.getDiscountPercentage());
    this.setActive(promotion.isActive());
    this.setDescription(promotion.getDescription());
    this.setStoreId(promotion.getStore().getId());
    this.setProductIds(productIds);
  }
}
