package ispp.project.dondesiempre.services.outfits;

import ispp.project.dondesiempre.exceptions.ResourceNotFoundException;
import ispp.project.dondesiempre.models.outfits.OutfitTag;
import ispp.project.dondesiempre.repositories.outfits.OutfitTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
