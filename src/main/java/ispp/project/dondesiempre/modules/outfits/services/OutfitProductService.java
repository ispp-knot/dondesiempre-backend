package ispp.project.dondesiempre.modules.outfits.services;

import ispp.project.dondesiempre.modules.common.exceptions.ResourceNotFoundException;
import ispp.project.dondesiempre.modules.outfits.models.OutfitProduct;
import ispp.project.dondesiempre.modules.outfits.repositories.OutfitProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OutfitProductService {

    private final OutfitProductRepository outfitProductRepository;

    @Transactional(readOnly = true)
    public List<OutfitProduct> findOutfitProductsById(UUID outfitId) {
        return outfitProductRepository.findOutfitProductsById(outfitId);
    }

    @Transactional(readOnly = true)
    public List<Integer> findOutfitProductIndicesById(UUID outfitId) {
        return outfitProductRepository.findOutfitProductIndicesById(outfitId);
    }

    @Transactional
    public OutfitProduct save(OutfitProduct outfitProduct) {
        return outfitProductRepository.save(outfitProduct);
    }

    @Transactional
    public List<OutfitProduct> saveAll(List<OutfitProduct> outfitProducts) {
        return outfitProductRepository.saveAll(outfitProducts);
    }

    @Transactional(readOnly = true)
    public OutfitProduct findProductRelation(UUID outfitId, UUID productId) {
        return outfitProductRepository.findProductRelation(outfitId, productId).orElseThrow(() ->
                new ResourceNotFoundException("Requested product does not belong to outfit."));
    }

    @Transactional
    public void delete(OutfitProduct outfitProduct) {
        outfitProductRepository.delete(outfitProduct);
    }

    @Transactional
    public void deleteById(UUID id) {
        outfitProductRepository.deleteById(id);
    }
}
