package ispp.project.dondesiempre.modules.outfits.services;

import ispp.project.dondesiempre.modules.auth.services.AuthService;
import ispp.project.dondesiempre.modules.common.exceptions.InvalidRequestException;
import ispp.project.dondesiempre.modules.common.exceptions.ResourceNotFoundException;
import ispp.project.dondesiempre.modules.common.exceptions.UnauthorizedException;
import ispp.project.dondesiempre.modules.outfits.dtos.OutfitCreationDTO;
import ispp.project.dondesiempre.modules.outfits.dtos.OutfitCreationProductDTO;
import ispp.project.dondesiempre.modules.outfits.dtos.OutfitDTO;
import ispp.project.dondesiempre.modules.outfits.dtos.OutfitSortDTO;
import ispp.project.dondesiempre.modules.outfits.dtos.OutfitTagDTO;
import ispp.project.dondesiempre.modules.outfits.dtos.OutfitUpdateDTO;
import ispp.project.dondesiempre.modules.outfits.models.Outfit;
import ispp.project.dondesiempre.modules.outfits.models.OutfitProduct;
import ispp.project.dondesiempre.modules.outfits.models.OutfitTag;
import ispp.project.dondesiempre.modules.outfits.models.OutfitTagRelation;
import ispp.project.dondesiempre.modules.outfits.repositories.OutfitProductRepository;
import ispp.project.dondesiempre.modules.outfits.repositories.OutfitRepository;
import ispp.project.dondesiempre.modules.outfits.repositories.OutfitTagRepository;
import ispp.project.dondesiempre.modules.products.models.Product;
import ispp.project.dondesiempre.modules.products.services.ProductService;
import ispp.project.dondesiempre.modules.stores.models.Store;
import ispp.project.dondesiempre.modules.stores.services.StoreService;
import ispp.project.dondesiempre.utils.cloudinary.CloudinaryService;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
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
  private final OutfitTagRepository outfitTagRepository;
  private final OutfitProductService outfitProductService;
  private final OutfitTagRelationService outfitTagRelationService;
  private final OutfitTagService outfitTagService;

  private final ProductService productService;
  private final StoreService storeService;
  private final AuthService authService;

  private final CloudinaryService cloudinaryService;

  private final ApplicationContext applicationContext;

  @Transactional(readOnly = true, rollbackFor = ResourceNotFoundException.class)
  public Outfit findById(UUID id) throws ResourceNotFoundException {
    return outfitRepository
        .findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Outfit with ID " + id + "not found."));
  }

  @Transactional(readOnly = true)
  public List<OutfitTagDTO> findTagsByOutfitId(UUID id) {
    return outfitTagService.findOutfitTagsById(id).stream().map(OutfitTagDTO::new).toList();
  }

  @Transactional(readOnly = true)
  public List<OutfitProduct> findOutfitProductsByOutfitId(UUID id) {
    return outfitProductService.findOutfitProductsById(id);
  }

  @Transactional(readOnly = true)
  public List<Outfit> findByStore(Store store) {
    return outfitRepository.findByStoreIdOrderByIndexAsc(store.getId());
  }

  @Transactional(readOnly = true)
  public OutfitDTO findByIdAsDTO(UUID id) {
    Outfit outfit = applicationContext.getBean(OutfitService.class).findById(id);
    List<OutfitTagDTO> tags =
        applicationContext.getBean(OutfitService.class).findTagsByOutfitId(id);
    List<OutfitProduct> products = outfitProductRepository.findByOutfitIdWithDetails(id);
    return new OutfitDTO(outfit, tags, products);
  }

  @Transactional(readOnly = true)
  public List<OutfitDTO> findByStoreIdAsDTO(UUID storeId) {
    return findByStoreIdAndNameAsDTO(storeId, null);
  }

  @Transactional(readOnly = true)
  public List<OutfitDTO> findByStoreIdAndNameAsDTO(UUID storeId, String name) {
    List<Outfit> outfits =
        name != null && !name.isBlank()
            ? outfitRepository.findByStoreIdAndNameContainingIgnoreCase(storeId, name)
            : outfitRepository.findByStoreIdOrderByIndexAsc(storeId);
    if (outfits.isEmpty()) return List.of();

    List<UUID> ids = outfits.stream().map(Outfit::getId).toList();

    Map<UUID, List<OutfitTagDTO>> tagsByOutfit =
        outfitTagRepository.findTagEntriesByOutfitIds(ids).stream()
            .collect(
                Collectors.groupingBy(
                    OutfitTagRepository.OutfitTagEntry::getOutfitId,
                    Collectors.mapping(
                        entry -> new OutfitTagDTO(entry.getTagName()), Collectors.toList())));

    Map<UUID, List<OutfitProduct>> productsByOutfit =
        outfitProductRepository.findByOutfitIdsWithDetails(ids).stream()
            .collect(Collectors.groupingBy(op -> op.getOutfit().getId()));

    return outfits.stream()
        .map(
            o ->
                new OutfitDTO(
                    o,
                    tagsByOutfit.getOrDefault(o.getId(), List.of()),
                    productsByOutfit.getOrDefault(o.getId(), List.of())))
        .toList();
  }

  @Transactional(
      rollbackFor = {
        UnauthorizedException.class,
        ResourceNotFoundException.class,
        InvalidRequestException.class
      })
  public Outfit create(UUID storeId, OutfitCreationDTO dto, MultipartFile image)
      throws UnauthorizedException, ResourceNotFoundException, InvalidRequestException {
    Outfit outfit;
    UUID outfitId;
    List<Outfit> outfitsOfStore;

    outfit = new Outfit();

    outfit.setName(dto.getName());
    outfit.setDescription(dto.getDescription());

    if (image != null && !image.isEmpty()) {
      outfit.setImage(cloudinaryService.upload(image));
    }
    Store store = storeService.findById(storeId);
    authService.assertUserOwnsStore(store);
    outfit.setStore(store);

    outfitsOfStore = applicationContext.getBean(OutfitService.class).findByStore(store);
    outfit.setIndex(outfitsOfStore.stream().mapToInt(Outfit::getIndex).max().orElse(-1) + 1);

    if (dto.getProducts() == null || (dto.getProducts() != null && dto.getProducts().size() <= 0)) {
      throw new InvalidRequestException("An outfit cannot be created without products.");
    }
    outfit.setDiscountPercentage(null);

    outfit = outfitRepository.save(outfit);
    outfitId = outfit.getId();

    if (dto.getTags() != null) {
      dto.getTags()
          .forEach(tag -> applicationContext.getBean(OutfitService.class).addTag(outfitId, tag));
    }
    dto.getProducts().stream()
        .forEach(
            product ->
                applicationContext.getBean(OutfitService.class).addProduct(outfitId, product));

    return outfit;
  }

  @Transactional(
      rollbackFor = {
        UnauthorizedException.class,
        ResourceNotFoundException.class,
        InvalidRequestException.class
      })
  public Outfit update(UUID id, OutfitUpdateDTO dto, MultipartFile image)
      throws UnauthorizedException, ResourceNotFoundException, InvalidRequestException {
    Outfit outfitToUpdate;

    outfitToUpdate = applicationContext.getBean(OutfitService.class).findById(id);
    authService.assertUserOwnsStore(outfitToUpdate.getStore());

    outfitToUpdate.setName(dto.getName());
    outfitToUpdate.setDescription(dto.getDescription());
    outfitToUpdate.setDiscountPercentage(dto.getDiscountPercentage());

    if (image != null && !image.isEmpty()) {
      outfitToUpdate.setImage(cloudinaryService.upload(image));
    }
    return outfitRepository.save(outfitToUpdate);
  }

  @Transactional(rollbackFor = {UnauthorizedException.class, ResourceNotFoundException.class})
  public OutfitTagDTO addTag(UUID outfitId, OutfitTagDTO tagDTO)
      throws UnauthorizedException, ResourceNotFoundException {
    Outfit outfit;
    OutfitTag tag;
    OutfitTagRelation outfitTag;

    outfit = applicationContext.getBean(OutfitService.class).findById(outfitId);
    authService.assertUserOwnsStore(outfit.getStore());
    tag = outfitTagService.findOrCreate(tagDTO.getName());

    outfitTag = new OutfitTagRelation();
    outfitTag.setOutfit(outfit);
    outfitTag.setTag(tag);
    outfitTagRelationService.save(outfitTag);

    return new OutfitTagDTO(tag.getName());
  }

  @Transactional(rollbackFor = {UnauthorizedException.class, ResourceNotFoundException.class})
  public void removeTag(UUID outfitId, OutfitTagDTO tagDTO)
      throws UnauthorizedException, ResourceNotFoundException {
    Outfit outfit;
    OutfitTag tag;
    OutfitTagRelation relation;

    outfit = applicationContext.getBean(OutfitService.class).findById(outfitId);
    authService.assertUserOwnsStore(outfit.getStore());
    tag = outfitTagService.findByName(tagDTO.getName());

    relation = outfitTagRelationService.findTagRelation(outfit.getId(), tag.getId());
    outfitTagRelationService.delete(relation);
  }

  @Transactional(
      rollbackFor = {
        UnauthorizedException.class,
        ResourceNotFoundException.class,
        InvalidRequestException.class
      })
  public OutfitProduct addProduct(UUID outfitId, OutfitCreationProductDTO dto)
      throws UnauthorizedException, ResourceNotFoundException, InvalidRequestException {
    Outfit outfit;
    Product product;
    OutfitProduct outfitProduct;
    List<Product> productsOfOutfit;
    List<Integer> productIndicesOfOutfit;

    outfit = applicationContext.getBean(OutfitService.class).findById(outfitId);
    authService.assertUserOwnsStore(outfit.getStore());
    product = productService.getProductById(dto.getProductId());

    productsOfOutfit = productService.getOutfitProductsById(outfitId);
    productsOfOutfit.add(product);

    if (productsOfOutfit.stream().map(prod -> prod.getStore().getId()).distinct().count() > 1L) {
      throw new InvalidRequestException("All products in an outfit must belong to the same store.");
    }
    productIndicesOfOutfit = outfitProductService.findOutfitProductIndicesById(outfitId);
    productIndicesOfOutfit.add(dto.getIndex());

    if (productsOfOutfit.stream().distinct().count() < productIndicesOfOutfit.size()) {
      throw new InvalidRequestException("All products in an outfit must have distinct indices.");
    }
    outfitProduct = new OutfitProduct();
    outfitProduct.setIndex(dto.getIndex());
    outfitProduct.setOutfit(outfit);
    outfitProduct.setProduct(product);
    outfitProductService.save(outfitProduct);

    return outfitProduct;
  }

  @Transactional(
      rollbackFor = {
        UnauthorizedException.class,
        ResourceNotFoundException.class,
        InvalidRequestException.class
      })
  public void removeProduct(UUID outfitId, Product product)
      throws UnauthorizedException, ResourceNotFoundException, InvalidRequestException {
    Outfit outfit;
    OutfitProduct relation;

    outfit = applicationContext.getBean(OutfitService.class).findById(outfitId);
    authService.assertUserOwnsStore(outfit.getStore());

    if (outfitProductService.findOutfitProductsById(outfitId).size() <= 2) {
      throw new InvalidRequestException("Outfits must contain at least two products.");
    }
    relation = outfitProductService.findProductRelation(outfit.getId(), product.getId());
    outfitProductService.delete(relation);
  }

  @Transactional(rollbackFor = {UnauthorizedException.class, ResourceNotFoundException.class})
  public void sortProducts(UUID outfitId, List<OutfitCreationProductDTO> products)
      throws UnauthorizedException, ResourceNotFoundException {
    Outfit outfit;
    List<OutfitProduct> relations;

    outfit = applicationContext.getBean(OutfitService.class).findById(outfitId);
    authService.assertUserOwnsStore(outfit.getStore());
    relations = outfitProductService.findOutfitProductsById(outfitId);

    for (OutfitProduct relation : relations) {
      OutfitCreationProductDTO product;

      product =
          products.stream()
              .filter(p -> relation.getProduct().getId().equals(p.getProductId()))
              .findFirst()
              .orElseThrow(
                  () ->
                      new ResourceNotFoundException(
                          "Could not find association entity between requested outfit and product."));
      relation.setIndex(product.getIndex());
    }
    outfitProductService.saveAll(relations);
  }

  @Transactional(rollbackFor = {UnauthorizedException.class, ResourceNotFoundException.class})
  public void delete(UUID id) throws UnauthorizedException, ResourceNotFoundException {
    Outfit outfit = applicationContext.getBean(OutfitService.class).findById(id);
    authService.assertUserOwnsStore(outfit.getStore());
    outfitProductService.findOutfitProductsById(id).stream()
        .forEach(p -> outfitProductService.deleteById(p.getId()));
    outfitTagRelationService.findOutfitTagsById(id).stream()
        .forEach(t -> outfitTagRelationService.deleteById(t.getId()));
    outfitRepository.deleteById(id);
  }

  @Transactional(
      rollbackFor = {
        UnauthorizedException.class,
        ResourceNotFoundException.class,
        InvalidRequestException.class
      })
  public void sortOutfits(UUID storeId, List<OutfitSortDTO> dtos)
      throws UnauthorizedException, ResourceNotFoundException, InvalidRequestException {
    Store store;
    List<Outfit> outfits;

    store = storeService.findById(storeId);
    authService.assertUserOwnsStore(store);

    if (dtos.stream().mapToInt(OutfitSortDTO::getIndex).distinct().count() < dtos.size()) {
      throw new InvalidRequestException("All outfits must have different indices.");
    }

    for (OutfitSortDTO dto : dtos) {
      Outfit outfit;

      outfit = applicationContext.getBean(OutfitService.class).findById(dto.getId());

      if (outfit.getStore().getId() != storeId) {
        throw new InvalidRequestException("All outfits must belong to the requested store.");
      }
      outfit.setIndex(dto.getIndex());
      outfitRepository.save(outfit);
    }
  }
}
