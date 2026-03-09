package ispp.project.dondesiempre.modules.outfits.services;

import ispp.project.dondesiempre.modules.common.exceptions.ResourceNotFoundException;
import ispp.project.dondesiempre.modules.outfits.models.OutfitTagRelation;
import ispp.project.dondesiempre.modules.outfits.repositories.OutfitTagRelationRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OutfitTagRelationService {

  private final OutfitTagRelationRepository outfitTagRelationRepository;

  @Transactional
  public OutfitTagRelation save(OutfitTagRelation outfitTagRelation) {
    return outfitTagRelationRepository.save(outfitTagRelation);
  }

  @Transactional(readOnly = true)
  public OutfitTagRelation findTagRelation(UUID outfitId, UUID tagId) {
    return outfitTagRelationRepository
        .findTagRelation(outfitId, tagId)
        .orElseThrow(
            () -> new ResourceNotFoundException("Requested tag does not belong to outfit."));
  }

  @Transactional(readOnly = true)
  public List<OutfitTagRelation> findOutfitTagsById(UUID outfitId) {
    return outfitTagRelationRepository.findOutfitTagsById(outfitId);
  }

  @Transactional
  public void delete(OutfitTagRelation outfitTagRelation) {
    outfitTagRelationRepository.delete(outfitTagRelation);
  }

  @Transactional
  public void deleteById(UUID id) {
    outfitTagRelationRepository.deleteById(id);
  }
}
