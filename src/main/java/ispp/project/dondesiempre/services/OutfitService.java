package ispp.project.dondesiempre.services;

import ispp.project.dondesiempre.models.outfits.Outfit;
import ispp.project.dondesiempre.repositories.outfits.OutfitRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OutfitService {
  private OutfitRepository outfitRepository;

  @Autowired
  public OutfitService(OutfitRepository outfitRepository) {
    this.outfitRepository = outfitRepository;
  }

  @Transactional(readOnly = true)
  public Outfit findById(Integer id) throws EntityNotFoundException {
    return outfitRepository.findById(id).orElseThrow(() -> new EntityNotFoundException());
  }

  @Transactional(readOnly = true)
  public List<Outfit> findByStore(Integer storeId) {
    return outfitRepository.findByStoreId(storeId);
  }
}
