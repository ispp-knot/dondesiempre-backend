package ispp.project.dondesiempre.services;

import ispp.project.dondesiempre.exceptions.InvalidRequestException;
import ispp.project.dondesiempre.exceptions.ResourceNotFoundException;
import ispp.project.dondesiempre.models.outfits.Outfit;
import ispp.project.dondesiempre.models.outfits.OutfitProduct;
import ispp.project.dondesiempre.models.outfits.OutfitTag;
import ispp.project.dondesiempre.models.outfits.OutfitTagRelation;
import ispp.project.dondesiempre.models.outfits.dto.OutfitCreationDTO;
import ispp.project.dondesiempre.models.outfits.dto.OutfitCreationProductDTO;
import ispp.project.dondesiempre.models.outfits.dto.OutfitDTO;
import ispp.project.dondesiempre.models.outfits.dto.OutfitProductDTO;
import ispp.project.dondesiempre.models.outfits.dto.OutfitUpdateDTO;
import ispp.project.dondesiempre.models.products.Product;
import ispp.project.dondesiempre.repositories.outfits.OutfitProductRepository;
import ispp.project.dondesiempre.repositories.outfits.OutfitRepository;
import ispp.project.dondesiempre.repositories.outfits.OutfitTagRelationRepository;
import ispp.project.dondesiempre.repositories.products.ProductRepository;
import ispp.project.dondesiempre.repositories.storefronts.StorefrontRepository;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OutfitService {
  private final OutfitRepository outfitRepository;
  private final OutfitProductRepository outfitProductRepository;
  private final OutfitTagRelationRepository outfitTagRelationRepository;

  private final ProductRepository productRepository;
  private final StorefrontRepository storefrontRepository;

  private final OutfitTagService outfitTagService;

  @Transactional(readOnly = true)
  public Outfit findById(Integer id) throws ResourceNotFoundException {
    return outfitRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Outfit with ID " + id + "not found."));
  }

  @Transactional(readOnly = true)
  public OutfitDTO findByIdToDTO(Integer id) throws ResourceNotFoundException {
    return new OutfitDTO(
        findById(id),
        outfitRepository.findOutfitTagsById(id),
        outfitRepository.findOutfitOutfitProductsById(id));
  }

  @Transactional(readOnly = true)
  public List<OutfitDTO> findByStore(Integer storeId) {
    return outfitRepository.findByStoreId(storeId).stream()
        .map(
            outfit ->
                new OutfitDTO(
                    outfit,
                    outfitRepository.findOutfitTagsById(outfit.getId()),
                    outfitRepository.findOutfitOutfitProductsById(outfit.getId())))
        .collect(Collectors.toList());
  }

  @Transactional
  public OutfitDTO create(OutfitCreationDTO dto) throws InvalidRequestException {
    Outfit outfit;
    Integer outfitId;

    outfit = new Outfit();

    outfit.setName(dto.getName());
    outfit.setDescription(dto.getDescription());
    outfit.setIndex(dto.getIndex());
    outfit.setImage(dto.getImage());
    outfit.setDiscount(BigDecimal.ZERO);
    outfit.setStorefront(
        storefrontRepository
            .findById(dto.getStorefrontid())
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "Storefront with ID" + dto.getStorefrontid() + "not found.")));

    if (dto.getProducts() == null || (dto.getProducts() != null && dto.getProducts().size() <= 0)) {
      throw new InvalidRequestException("An outfit cannot be created without products.");
    }
    outfit = outfitRepository.save(outfit);
    outfitId = outfit.getId();

    dto.getTags().stream().forEach(name -> addTag(outfitId, name));
    dto.getProducts().stream().forEach(product -> addProduct(outfitId, product));

    return new OutfitDTO(
        outfit,
        outfitRepository.findOutfitTagsById(outfit.getId()),
        outfitRepository.findOutfitOutfitProductsById(outfit.getId()));
  }

  @Transactional
  public OutfitDTO update(Integer id, OutfitUpdateDTO dto) throws ResourceNotFoundException {
    Outfit outfitToUpdate;

    outfitToUpdate = findById(id);

    outfitToUpdate.setName(dto.getName());
    outfitToUpdate.setDescription(dto.getDescription());
    outfitToUpdate.setDiscount(dto.getDiscount());
    outfitToUpdate.setImage(dto.getImage());
    outfitToUpdate.setIndex(dto.getIndex());

    return new OutfitDTO(
        outfitRepository.save(outfitToUpdate),
        outfitRepository.findOutfitTagsById(id),
        outfitRepository.findOutfitOutfitProductsById(id));
  }

  @Transactional
  public String addTag(Integer outfitId, String tagName) throws ResourceNotFoundException {
    Outfit outfit;
    OutfitTag tag;
    OutfitTagRelation outfitTag;

    outfit = findById(outfitId);
    tag = outfitTagService.findOrCreateTag(tagName);

    outfitTag = new OutfitTagRelation();
    outfitTag.setOutfit(outfit);
    outfitTag.setTag(tag);
    outfitTagRelationRepository.save(outfitTag);

    return tag.getName();
  }

  @Transactional
  public OutfitProductDTO addProduct(Integer outfitId, OutfitCreationProductDTO dto)
      throws ResourceNotFoundException, InvalidRequestException {
    Outfit outfit;
    Product product;
    OutfitProduct outfitProduct;
    List<Product> productsOfOutfit;
    List<Integer> productIndicesOfOutfit;

    outfit = findById(outfitId);
    product =
        productRepository.findById(dto.getId()).orElseThrow(() -> new EntityNotFoundException());

    productsOfOutfit = outfitRepository.findOutfitProductsById(outfitId);
    productsOfOutfit.add(product);

    if (productsOfOutfit.stream().mapToInt(prod -> prod.getStore().getId()).distinct().count()
        > 1L) {
      throw new InvalidRequestException("All products in an outfit must belong to the same store.");
    }
    productIndicesOfOutfit = outfitRepository.findOutfitProductIndicesById(outfitId);
    productIndicesOfOutfit.add(dto.getIndex());

    if (productsOfOutfit.stream().distinct().count() < productIndicesOfOutfit.size()) {
      throw new InvalidRequestException("All products in an outfit must have distinct indices.");
    }
    outfitProduct = new OutfitProduct();
    outfitProduct.setIndex(dto.getIndex());
    outfitProduct.setOutfit(outfit);
    outfitProduct.setProduct(product);
    outfitProductRepository.save(outfitProduct);

    return new OutfitProductDTO(outfitProduct);
  }

  @Transactional
  public void delete(Integer id) {
    outfitRepository.deleteById(id);
  }
}
