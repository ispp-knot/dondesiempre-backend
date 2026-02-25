package ispp.project.dondesiempre.models.promotions.dto;

import ispp.project.dondesiempre.models.promotions.Promotion;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PromotionDTO {

  private Integer id;
  private String name;
  private Integer discountPercentage;
  private boolean isActive;
  private String description;
  private Integer storeId;
  private List<Integer> productIds;

  public static PromotionDTO fromPromotion(Promotion promotion, List<Integer> productIds) {
    PromotionDTO dto = new PromotionDTO();
    dto.setId(promotion.getId());
    dto.setName(promotion.getName());
    dto.setDiscountPercentage(promotion.getDiscountPercentage());
    dto.setActive(promotion.isActive());
    dto.setDescription(promotion.getDescription());
    dto.setStoreId(promotion.getStore().getId());
    dto.setProductIds(productIds);
    return dto;
  }
}
