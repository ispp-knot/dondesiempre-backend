package ispp.project.dondesiempre.modules.promotions.controllers;

import ispp.project.dondesiempre.modules.promotions.dtos.PromotionCreationDTO;
import ispp.project.dondesiempre.modules.promotions.dtos.PromotionDTO;
import ispp.project.dondesiempre.modules.promotions.dtos.PromotionUpdateDTO;
import ispp.project.dondesiempre.modules.promotions.models.Promotion;
import ispp.project.dondesiempre.modules.promotions.services.PromotionService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PromotionController {

  private final PromotionService promotionService;

  @PostMapping("/api/v1/promotions")
  public ResponseEntity<PromotionDTO> createPromotion(@RequestBody PromotionCreationDTO dto) {
    Promotion promotion = promotionService.savePromotion(dto);
    PromotionDTO promotionDTO =
        new PromotionDTO(
            promotion, promotionService.getAllProductsDTOByPromotionId(promotion.getId()));
    return ResponseEntity.ok(promotionDTO);
  }

  @GetMapping("/api/v1/promotions")
  public ResponseEntity<List<PromotionDTO>> getAllPromotions() {
    return ResponseEntity.ok(
        promotionService.getAllPromotions().stream()
            .map(
                promotion ->
                    new PromotionDTO(
                        promotion,
                        promotionService.getAllProductsDTOByPromotionId(promotion.getId())))
            .toList());
  }

  @GetMapping("/api/v1/promotions/{id}")
  public ResponseEntity<PromotionDTO> getPromotionById(@PathVariable UUID id) {
    Promotion promotion = promotionService.getPromotionById(id);
    PromotionDTO promotionDTO =
        new PromotionDTO(
            promotion, promotionService.getAllProductsDTOByPromotionId(promotion.getId()));
    return ResponseEntity.ok(promotionDTO);
  }

  @PutMapping("/api/v1/promotions/{id}")
  public ResponseEntity<PromotionDTO> updatePromotion(
      @PathVariable UUID id, @RequestBody PromotionUpdateDTO updateDTO) {

    Promotion promotion = promotionService.updatePromotion(id, updateDTO);
    return ResponseEntity.ok(
        new PromotionDTO(
            promotion, promotionService.getAllProductsDTOByPromotionId(promotion.getId())));
  }

  @DeleteMapping("/api/v1/promotions/{id}")
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
                    new PromotionDTO(
                        promotion,
                        promotionService.getAllProductsDTOByPromotionId(promotion.getId())))
            .toList());
  }
}
