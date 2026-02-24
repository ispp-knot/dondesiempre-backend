package ispp.project.dondesiempre.controllers.promotions;

import ispp.project.dondesiempre.models.promotions.Promotion;
import ispp.project.dondesiempre.models.promotions.dto.PromotionCreationDTO;
import ispp.project.dondesiempre.services.promotions.PromotionService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/promotions")
@RequiredArgsConstructor
public class PromotionController {

  private final PromotionService promotionService;

  @PostMapping
  public ResponseEntity<Promotion> createPromotion(@RequestBody PromotionCreationDTO dto) {
    Promotion promotion = promotionService.savePromotion(dto);
    return ResponseEntity.ok(promotion);
  }

  @GetMapping
  public ResponseEntity<List<Promotion>> getAllPromotions() {
    return ResponseEntity.ok(promotionService.getAllPromotions());
  }

  @GetMapping("/{id}")
  public ResponseEntity<Promotion> getPromotionById(@PathVariable Integer id) {
    return ResponseEntity.ok(promotionService.getPromotionById(id));
  }

  @PatchMapping("/{id}/discount")
  public ResponseEntity<Promotion> updateDiscount(
      @PathVariable Integer id, @RequestParam Integer discountPercentage) {

    Promotion promotion = promotionService.updatePromotionDiscount(id, discountPercentage);
    return ResponseEntity.ok(promotion);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deletePromotion(@PathVariable Integer id) {
    promotionService.deletePromotion(id);
    return ResponseEntity.noContent().build();
  }
}
