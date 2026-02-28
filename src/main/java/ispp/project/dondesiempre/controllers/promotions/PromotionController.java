package ispp.project.dondesiempre.controllers.promotions;

import ispp.project.dondesiempre.models.promotions.Promotion;
import ispp.project.dondesiempre.models.promotions.dto.PromotionCreationDTO;
import ispp.project.dondesiempre.models.promotions.dto.PromotionDTO;
import ispp.project.dondesiempre.services.promotions.PromotionService;
import java.util.List;
import java.util.UUID;
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
@RequestMapping("/api/v1/promotions")
@RequiredArgsConstructor
public class PromotionController {

  private final PromotionService promotionService;

  @PostMapping
  public ResponseEntity<PromotionDTO> createPromotion(@RequestBody PromotionCreationDTO dto) {
    Promotion promotion = promotionService.savePromotion(dto);
    PromotionDTO promotionDTO =
        PromotionDTO.fromPromotion(
            promotion, promotionService.getAllProductsByPromotionId(promotion.getId()));
    return ResponseEntity.ok(promotionDTO);
  }

  @GetMapping
  public ResponseEntity<List<PromotionDTO>> getAllPromotions() {
    return ResponseEntity.ok(
        promotionService.getAllPromotions().stream()
            .map(
                promotion ->
                    PromotionDTO.fromPromotion(
                        promotion, promotionService.getAllProductsByPromotionId(promotion.getId())))
            .toList());
  }

  @GetMapping("/{id}")
  public ResponseEntity<PromotionDTO> getPromotionById(@PathVariable UUID id) {
    Promotion promotion = promotionService.getPromotionById(id);
    PromotionDTO promotionDTO =
        PromotionDTO.fromPromotion(
            promotion, promotionService.getAllProductsByPromotionId(promotion.getId()));
    return ResponseEntity.ok(promotionDTO);
  }

  @PatchMapping("/{id}/discount")
  public ResponseEntity<PromotionDTO> updateDiscount(
      @PathVariable UUID id, @RequestParam Integer discountPercentage) {

    Promotion promotion = promotionService.updatePromotionDiscount(id, discountPercentage);
    return ResponseEntity.ok(
        PromotionDTO.fromPromotion(
            promotion, promotionService.getAllProductsByPromotionId(promotion.getId())));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deletePromotion(@PathVariable UUID id) {
    promotionService.deletePromotion(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/api/v1/stores/{storeId}/promotions")
  public ResponseEntity<List<PromotionDTO>> getPromotionsByStoreId(@PathVariable UUID storeId) {
    return ResponseEntity.ok(
        promotionService.getPromotionsByStoreId(storeId).stream()
            .map(
                promotion ->
                    PromotionDTO.fromPromotion(
                        promotion, promotionService.getAllProductsByPromotionId(promotion.getId())))
            .toList());
  }
}
