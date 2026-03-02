package ispp.project.dondesiempre.services.outfits;

import ispp.project.dondesiempre.exceptions.InvalidRequestException;
import ispp.project.dondesiempre.exceptions.ResourceNotFoundException;
import ispp.project.dondesiempre.models.outfits.Outfit;
import ispp.project.dondesiempre.models.outfits.OutfitProduct;
import ispp.project.dondesiempre.models.outfits.OutfitTag;
import ispp.project.dondesiempre.models.outfits.OutfitTagRelation;
import ispp.project.dondesiempre.models.outfits.dto.OutfitCreationDTO;
import ispp.project.dondesiempre.models.outfits.dto.OutfitCreationProductDTO;
import ispp.project.dondesiempre.models.outfits.dto.OutfitUpdateDTO;
import ispp.project.dondesiempre.models.products.Product;
import ispp.project.dondesiempre.models.storefronts.Storefront;
import ispp.project.dondesiempre.models.stores.Store;
import ispp.project.dondesiempre.repositories.outfits.OutfitProductRepository;
import ispp.project.dondesiempre.repositories.outfits.OutfitRepository;
import ispp.project.dondesiempre.repositories.outfits.OutfitTagRelationRepository;
import ispp.project.dondesiempre.services.CloudinaryService;
import ispp.project.dondesiempre.services.UserService;
import ispp.project.dondesiempre.services.products.ProductService;
import ispp.project.dondesiempre.services.storefronts.StorefrontService;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class OutfitService {
  private final OutfitRepository outfitRepository;
  private final OutfitProductRepository outfitProductRepository;
  private final OutfitTagRelationRepository outfitTagRelationRepository;

  private final ProductService productService;
  private final StorefrontService storefrontService;
  private final UserService userService;

  private final OutfitTagService outfitTagService;
  private final CloudinaryService cloudinaryService;

  private final ApplicationContext applicationContext;

  @Transactional(readOnly = true, rollbackFor = ResourceNotFoundException.class)
  public Outfit findById(UUID id) throws ResourceNotFoundException {
    return outfitRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Outfit with ID " + id + "not found."));
  }

  @Transactional(readOnly = true)
  public List<String> findTagsByOutfitId(UUID id) {
    return outfitRepository.findOutfitTagsById(id);
  }

  @Transactional(readOnly = true)
  public List<OutfitProduct> findOutfitProductsByOutfitId(UUID id) {
    return outfitRepository.findOutfitOutfitProductsById(id);
  }

  @Transactional(readOnly = true)
  public List<Outfit> findByStore(Store store) {
    return outfitRepository.findByStoreId(store.getId());
  }

  @Transactional(readOnly = true)
  public List<Outfit> findByStorefront(Storefront storefront) {
    return outfitRepository.findByStorefrontId(storefront.getId());
  }

  @Transactional(rollbackFor = InvalidRequestException.class)
  public Outfit create(OutfitCreationDTO dto, MultipartFile image) throws InvalidRequestException {
    Outfit outfit;
    UUID outfitId;

    outfit = new Outfit();

    outfit.setName(dto.getName());
    outfit.setDescription(dto.getDescription());
    outfit.setIndex(dto.getIndex());
    if (image != null && !image.isEmpty()) {
      outfit.setImage(cloudinaryService.upload(image));
    }
    Storefront storefront = storefrontService.findById(dto.getStorefrontId());
    userService.assertUserOwnsStore(storefront.getStore());
    outfit.setStorefront(storefront);

    if (dto.getProducts() == null || (dto.getProducts() != null && dto.getProducts().size() <= 0)) {
      throw new InvalidRequestException("An outfit cannot be created without products.");
    }
    outfit.setDiscountedPriceInCents(calculatePriceOnCreation(dto.getProducts()));

    outfit = outfitRepository.save(outfit);
    outfitId = outfit.getId();

    dto.getTags().stream().forEach(name -> addTag(outfitId, name));
    dto.getProducts().stream().forEach(product -> addProduct(outfitId, product));

    return outfit;
  }

  private Integer calculatePriceOnCreation(List<OutfitCreationProductDTO> dtos)
      throws EntityNotFoundException {
    return dtos.stream()
        .mapToInt(dto -> productService.getProductById(dto.getId()).getDiscountedPriceInCents())
        .sum();
  }

  @Transactional(rollbackFor = ResourceNotFoundException.class)
  public Outfit update(UUID id, OutfitUpdateDTO dto, MultipartFile image)
      throws ResourceNotFoundException {
    Outfit outfitToUpdate;

    outfitToUpdate = applicationContext.getBean(OutfitService.class).findById(id);
    userService.assertUserOwnsStore(outfitToUpdate.getStorefront().getStore());

    outfitToUpdate.setName(dto.getName());
    outfitToUpdate.setDescription(dto.getDescription());
    outfitToUpdate.setDiscountedPriceInCents(dto.getDiscountedPriceInCents());
    if (image != null && !image.isEmpty()) {
      outfitToUpdate.setImage(cloudinaryService.upload(image));
    }
    outfitToUpdate.setIndex(dto.getIndex());

    return outfitRepository.save(outfitToUpdate);
  }

  @Transactional(rollbackFor = ResourceNotFoundException.class)
  public String addTag(UUID outfitId, String tagName) throws ResourceNotFoundException {
    Outfit outfit;
    OutfitTag tag;
    OutfitTagRelation outfitTag;

    outfit = applicationContext.getBean(OutfitService.class).findById(outfitId);
    userService.assertUserOwnsStore(outfit.getStorefront().getStore());
    tag = outfitTagService.findOrCreate(tagName);

    outfitTag = new OutfitTagRelation();
    outfitTag.setOutfit(outfit);
    outfitTag.setTag(tag);
    outfitTagRelationRepository.save(outfitTag);

    return tag.getName();
  }

  @Transactional(rollbackFor = ResourceNotFoundException.class)
  public void removeTag(UUID outfitId, String tagName) throws ResourceNotFoundException {
    Outfit outfit;
    OutfitTag tag;
    OutfitTagRelation relation;

    outfit = applicationContext.getBean(OutfitService.class).findById(outfitId);
    userService.assertUserOwnsStore(outfit.getStorefront().getStore());
    tag = outfitTagService.findByName(tagName);

    relation =
        outfitRepository
            .findTagRelation(outfit.getId(), tag.getId())
            .orElseThrow(
                () -> new ResourceNotFoundException("Requested tag does not belong to outfit."));
    outfitTagRelationRepository.delete(relation);
  }

  @Transactional(rollbackFor = {ResourceNotFoundException.class, InvalidRequestException.class})
  public OutfitProduct addProduct(UUID outfitId, OutfitCreationProductDTO dto)
      throws ResourceNotFoundException, InvalidRequestException {
    Outfit outfit;
    Product product;
    OutfitProduct outfitProduct;
    List<Product> productsOfOutfit;
    List<Integer> productIndicesOfOutfit;

    outfit = applicationContext.getBean(OutfitService.class).findById(outfitId);
    userService.assertUserOwnsStore(outfit.getStorefront().getStore());
    product = productService.getProductById(dto.getId());

    productsOfOutfit = outfitRepository.findOutfitProductsById(outfitId);
    productsOfOutfit.add(product);

    if (productsOfOutfit.stream().map(prod -> prod.getStore().getId()).distinct().count() > 1L) {
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

    return outfitProduct;
  }

  @Transactional(rollbackFor = ResourceNotFoundException.class)
  public void removeProduct(UUID outfitId, Product product) throws ResourceNotFoundException {
    Outfit outfit;
    OutfitProduct relation;

    outfit = applicationContext.getBean(OutfitService.class).findById(outfitId);
    userService.assertUserOwnsStore(outfit.getStorefront().getStore());
    relation =
        outfitRepository
            .findProductRelation(outfit.getId(), product.getId())
            .orElseThrow(
                () ->
                    new ResourceNotFoundException("Requested product does not belong to outfit."));
    outfitProductRepository.delete(relation);
  }

  @Transactional(rollbackFor = ResourceNotFoundException.class)
  public void sortProducts(UUID outfitId, List<OutfitCreationProductDTO> products)
      throws ResourceNotFoundException {
    Outfit outfit;
    List<OutfitProduct> relations;

    outfit = applicationContext.getBean(OutfitService.class).findById(outfitId);
    userService.assertUserOwnsStore(outfit.getStorefront().getStore());
    relations = outfitRepository.findOutfitOutfitProductsById(outfitId);

    for (OutfitProduct relation : relations) {
      OutfitCreationProductDTO product;

      product =
          products.stream()
              .filter(p -> relation.getProduct().getId().equals(p.getId()))
              .findFirst()
              .orElseThrow(
                  () ->
                      new ResourceNotFoundException(
                          "Could not find association entity between requested outfit and product."));
      relation.setIndex(product.getIndex());
    }
    outfitProductRepository.saveAll(relations);
  }

  @Transactional
  public void delete(UUID id) {
    Outfit outfit = applicationContext.getBean(OutfitService.class).findById(id);
    userService.assertUserOwnsStore(outfit.getStorefront().getStore());
    outfitRepository.findOutfitOutfitProductsById(id).stream()
        .forEach(p -> outfitProductRepository.deleteById(p.getId()));
    outfitRepository.findOutfitOutfitTagsById(id).stream()
        .forEach(t -> outfitTagRelationRepository.deleteById(t.getId()));
    outfitRepository.deleteById(id);
  }
}
