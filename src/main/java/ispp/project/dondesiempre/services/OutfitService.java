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
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OutfitService {
  private final OutfitRepository outfitRepository;
  private final OutfitProductRepository outfitProductRepository;
  private final OutfitTagRelationRepository outfitTagRelationRepository;

  private final ProductService productService;
  private final StorefrontService storefrontService;

  private final OutfitTagService outfitTagService;

  private final ApplicationContext applicationContext;

  @Transactional(readOnly = true, rollbackFor = ResourceNotFoundException.class)
  public Outfit findById(Integer id) throws ResourceNotFoundException {
    return outfitRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Outfit with ID " + id + "not found."));
  }

  @Transactional(readOnly = true, rollbackFor = ResourceNotFoundException.class)
  public OutfitDTO findByIdToDTO(Integer id) throws ResourceNotFoundException {
    return new OutfitDTO(
        applicationContext.getBean(OutfitService.class).findById(id),
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

  @Transactional(rollbackFor = InvalidRequestException.class)
  public OutfitDTO create(OutfitCreationDTO dto) throws InvalidRequestException {
    Outfit outfit;
    Integer outfitId;

    outfit = new Outfit();

    outfit.setName(dto.getName());
    outfit.setDescription(dto.getDescription());
    outfit.setIndex(dto.getIndex());
    outfit.setImage(dto.getImage());
    outfit.setStorefront(storefrontService.findById(dto.getStorefrontId()));

    if (dto.getProducts() == null || (dto.getProducts() != null && dto.getProducts().size() <= 0)) {
      throw new InvalidRequestException("An outfit cannot be created without products.");
    }
    outfit.setDiscountedPriceInCents(
        applicationContext
            .getBean(OutfitService.class)
            .calculatePriceOnCreation(dto.getProducts()));
    outfit = outfitRepository.save(outfit);
    outfitId = outfit.getId();

    dto.getTags().stream().forEach(name -> addTag(outfitId, name));
    dto.getProducts().stream().forEach(product -> addProduct(outfitId, product));

    return new OutfitDTO(
        outfit,
        outfitRepository.findOutfitTagsById(outfit.getId()),
        outfitRepository.findOutfitOutfitProductsById(outfit.getId()));
  }

  @Transactional(rollbackFor = EntityNotFoundException.class)
  private Integer calculatePriceOnCreation(List<OutfitCreationProductDTO> dtos)
      throws EntityNotFoundException {
    return dtos.stream()
        .mapToDouble(dto -> productService.findById(dto.getId()).getPrice())
        /* TODO: Temporary until the Product class is modified */
        .mapToInt(price -> Double.valueOf(price * 100.0).intValue())
        .sum();
  }

  @Transactional(rollbackFor = ResourceNotFoundException.class)
  public OutfitDTO update(Integer id, OutfitUpdateDTO dto) throws ResourceNotFoundException {
    Outfit outfitToUpdate;

    outfitToUpdate = applicationContext.getBean(OutfitService.class).findById(id);

    outfitToUpdate.setName(dto.getName());
    outfitToUpdate.setDescription(dto.getDescription());
    outfitToUpdate.setDiscountedPriceInCents(dto.getDiscountedPriceInCents());
    outfitToUpdate.setImage(dto.getImage());
    outfitToUpdate.setIndex(dto.getIndex());

    return new OutfitDTO(
        outfitRepository.save(outfitToUpdate),
        outfitRepository.findOutfitTagsById(id),
        outfitRepository.findOutfitOutfitProductsById(id));
  }

  @Transactional(rollbackFor = ResourceNotFoundException.class)
  public String addTag(Integer outfitId, String tagName) throws ResourceNotFoundException {
    Outfit outfit;
    OutfitTag tag;
    OutfitTagRelation outfitTag;

    outfit = applicationContext.getBean(OutfitService.class).findById(outfitId);

    try {
      tag = outfitTagService.findByName(tagName);
    } catch (ResourceNotFoundException e) {
      tag = outfitTagService.create(tagName);
    }
    outfitTag = new OutfitTagRelation();
    outfitTag.setOutfit(outfit);
    outfitTag.setTag(tag);
    outfitTagRelationRepository.save(outfitTag);

    return tag.getName();
  }

  @Transactional(rollbackFor = {ResourceNotFoundException.class, InvalidRequestException.class})
  public OutfitProductDTO addProduct(Integer outfitId, OutfitCreationProductDTO dto)
      throws ResourceNotFoundException, InvalidRequestException {
    Outfit outfit;
    Product product;
    OutfitProduct outfitProduct;
    List<Product> productsOfOutfit;
    List<Integer> productIndicesOfOutfit;

    outfit = applicationContext.getBean(OutfitService.class).findById(outfitId);
    product = productService.findById(dto.getId());

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
