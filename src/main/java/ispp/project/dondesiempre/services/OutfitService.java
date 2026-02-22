package ispp.project.dondesiempre.services;

import ispp.project.dondesiempre.models.outfits.Outfit;
import ispp.project.dondesiempre.models.outfits.OutfitOutfitTag;
import ispp.project.dondesiempre.models.outfits.OutfitProduct;
import ispp.project.dondesiempre.models.outfits.OutfitTag;
import ispp.project.dondesiempre.models.outfits.dto.OutfitCreationDTO;
import ispp.project.dondesiempre.models.outfits.dto.OutfitCreationProductDTO;
import ispp.project.dondesiempre.models.products.Product;
import ispp.project.dondesiempre.repositories.outfits.OutfitOutfitTagRepository;
import ispp.project.dondesiempre.repositories.outfits.OutfitProductRepository;
import ispp.project.dondesiempre.repositories.outfits.OutfitRepository;
import ispp.project.dondesiempre.repositories.products.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import java.security.InvalidParameterException;
import java.util.List;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OutfitService {
  private OutfitRepository outfitRepository;
  private OutfitProductRepository outfitProductRepository;
  private OutfitOutfitTagRepository outfitOutfitTagRepository;

  private ProductRepository productRepository;

  private OutfitTagService outfitTagService;

  @Autowired
  public OutfitService(
      OutfitRepository outfitRepository,
      OutfitProductRepository outfitProductRepository,
      OutfitOutfitTagRepository outfitOutfitTagRepository,
      ProductRepository productRepository,
      OutfitTagService outfitTagService) {
    this.outfitRepository = outfitRepository;
    this.outfitProductRepository = outfitProductRepository;
    this.outfitOutfitTagRepository = outfitOutfitTagRepository;

    this.productRepository = productRepository;

    this.outfitTagService = outfitTagService;
  }

  @Transactional(readOnly = true)
  public Outfit findById(Integer id) throws EntityNotFoundException {
    return outfitRepository.findById(id).orElseThrow(() -> new EntityNotFoundException());
  }

  @Transactional(readOnly = true)
  public List<Outfit> findByStore(Integer storeId) {
    return outfitRepository.findByStoreId(storeId);
  }

  @Transactional
  public Outfit create(OutfitCreationDTO dto) throws InvalidParameterException {
    Outfit outfit;
    Integer outfitId;

    outfit = new Outfit();
    outfit.setName(dto.getName());
    outfit.setIndex(dto.getIndex());
    outfit.setImage(dto.getImage());
    outfit.setDiscount(0.0);

    if (dto.getProducts() == null || (dto.getProducts() != null && dto.getProducts().size() <= 0)) {
      throw new InvalidParameterException("An outfit cannot be created without products.");
    }
    outfit = outfitRepository.save(outfit);
    outfitId = outfit.getId();

    dto.getTags().stream().forEach(name -> addTag(outfitId, name));
    dto.getProducts().stream().forEach(product -> addProduct(outfitId, product));

    return outfit;
  }

  @Transactional
  public Outfit update(Outfit outfit) {
    Outfit newOutfit;

    newOutfit = new Outfit();
    BeanUtils.copyProperties(outfit, newOutfit, "id");
    return outfitRepository.save(newOutfit);
  }

  @Transactional
  public OutfitTag addTag(Integer outfitId, String tagName) throws EntityNotFoundException {
    Outfit outfit;
    OutfitTag tag;
    OutfitOutfitTag outfitTag;

    outfit = findById(outfitId);
    tag = outfitTagService.findOrCreateTag(tagName);

    outfitTag = new OutfitOutfitTag();
    outfitTag.setOutfit(outfit);
    outfitTag.setTag(tag);
    outfitOutfitTagRepository.save(outfitTag);

    return tag;
  }

  @Transactional
  public Product addProduct(Integer outfitId, OutfitCreationProductDTO dto) {
    Outfit outfit;
    Product product;
    OutfitProduct outfitProduct;
    List<Product> productsOfOutfit;

    outfit = findById(outfitId);
    product =
        productRepository.findById(dto.getId()).orElseThrow(() -> new EntityNotFoundException());

    productsOfOutfit = outfitRepository.findOutfitProductsById(outfitId);
    productsOfOutfit.add(product);

    outfitProduct = new OutfitProduct();
    outfitProduct.setIndex(dto.getIndex());
    outfitProduct.setOutfit(outfit);
    outfitProduct.setProduct(product);
    outfitProductRepository.save(outfitProduct);

    return product;
  }

  @Transactional
  public void delete(Integer id) {
    outfitRepository.deleteById(id);
  }
}
