package ispp.project.dondesiempre.modules.promotions.dtos;

import ispp.project.dondesiempre.modules.products.dtos.ProductDTO;
import ispp.project.dondesiempre.modules.promotions.models.Promotion;
import java.time.LocalDate;
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
  private List<ProductDTO> products;
  private String promotionImageUrl;
  private LocalDate startDate;
  private LocalDate endDate;

  public PromotionDTO(Promotion promotion, List<ProductDTO> products) {
    this.id = promotion.getId();
    this.name = promotion.getName();
    this.discountPercentage = promotion.getDiscountPercentage();
    this.description = promotion.getDescription().orElse(null);
    this.storeId = promotion.getStore().getId();
    this.products = products;
    this.promotionImageUrl = promotion.getPromotionImageUrl().orElse(null);
    this.startDate = promotion.getStartDate();
    this.endDate = promotion.getEndDate();

    LocalDate today = LocalDate.now();
    boolean isAfterStart =
        promotion.getStartDate() == null || !today.isBefore(promotion.getStartDate());
    boolean isBeforeEnd = promotion.getEndDate() == null || !today.isAfter(promotion.getEndDate());
    this.isActive = promotion.isActive() && isAfterStart && isBeforeEnd;
  }
}
