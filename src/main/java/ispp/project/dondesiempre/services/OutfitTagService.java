package ispp.project.dondesiempre.services;

import ispp.project.dondesiempre.models.outfits.OutfitTag;
import ispp.project.dondesiempre.repositories.outfits.OutfitTagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OutfitTagService {
  private OutfitTagRepository outfitTagRepository;

  @Autowired
  public OutfitTagService(OutfitTagRepository outfitTagRepository) {
    this.outfitTagRepository = outfitTagRepository;
  }

  @Transactional
  public OutfitTag findOrCreateTag(String name) {
    OutfitTag tag;

    tag = outfitTagRepository.findByName(name);

    if (tag != null) {
      tag = new OutfitTag();
      tag.setName(name);
      tag = outfitTagRepository.save(tag);
    }
    return tag;
  }
}
