package ispp.project.dondesiempre.modules.outfits.services;

import ispp.project.dondesiempre.modules.common.exceptions.ResourceNotFoundException;
import ispp.project.dondesiempre.modules.outfits.models.OutfitTag;
import ispp.project.dondesiempre.modules.outfits.repositories.OutfitTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OutfitTagService {
  private final OutfitTagRepository outfitTagRepository;

  @Transactional(readOnly = true, rollbackFor = ResourceNotFoundException.class)
  public OutfitTag findByName(String tagName) throws ResourceNotFoundException {
    return outfitTagRepository
        .findByName(tagName)
        .orElseThrow(() -> new ResourceNotFoundException("Tag '" + tagName + "' not found."));
  }

  @Transactional
  public OutfitTag findOrCreate(String tagName) {
    return outfitTagRepository
        .findByName(tagName)
        .orElseGet(
            () -> {
              OutfitTag tag = new OutfitTag();
              tag.setName(tagName);
              return outfitTagRepository.save(tag);
            });
  }

  @Transactional(readOnly = true)
    public List<String> findOutfitTagsById(UUID outfitId) {
      return outfitTagRepository.findOutfitTagsById(outfitId);
  }
}
