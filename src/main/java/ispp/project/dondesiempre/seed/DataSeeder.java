package ispp.project.dondesiempre.seed;

import com.cloudinary.Cloudinary;
import ispp.project.dondesiempre.config.CloudinaryProperties;
import ispp.project.dondesiempre.modules.auth.models.User;
import ispp.project.dondesiempre.modules.auth.repositories.UserRepository;
import ispp.project.dondesiempre.modules.clients.repositories.ClientRepository;
import ispp.project.dondesiempre.modules.follows.repositories.StoreFollowerRepository;
import ispp.project.dondesiempre.modules.orders.models.Order;
import ispp.project.dondesiempre.modules.orders.models.OrderItem;
import ispp.project.dondesiempre.modules.orders.repositories.OrderRepository;
import ispp.project.dondesiempre.modules.outfits.models.Outfit;
import ispp.project.dondesiempre.modules.outfits.models.OutfitProduct;
import ispp.project.dondesiempre.modules.outfits.models.OutfitTag;
import ispp.project.dondesiempre.modules.outfits.models.OutfitTagRelation;
import ispp.project.dondesiempre.modules.outfits.repositories.OutfitProductRepository;
import ispp.project.dondesiempre.modules.outfits.repositories.OutfitRepository;
import ispp.project.dondesiempre.modules.outfits.repositories.OutfitTagRelationRepository;
import ispp.project.dondesiempre.modules.outfits.repositories.OutfitTagRepository;
import ispp.project.dondesiempre.modules.products.models.Product;
import ispp.project.dondesiempre.modules.products.models.ProductColor;
import ispp.project.dondesiempre.modules.products.models.ProductSize;
import ispp.project.dondesiempre.modules.products.models.ProductType;
import ispp.project.dondesiempre.modules.products.models.ProductVariant;
import ispp.project.dondesiempre.modules.products.repositories.ProductColorRepository;
import ispp.project.dondesiempre.modules.products.repositories.ProductRepository;
import ispp.project.dondesiempre.modules.products.repositories.ProductSizeRepository;
import ispp.project.dondesiempre.modules.products.repositories.ProductTypeRepository;
import ispp.project.dondesiempre.modules.products.repositories.ProductVariantRepository;
import ispp.project.dondesiempre.modules.stores.models.SocialNetwork;
import ispp.project.dondesiempre.modules.stores.models.Store;
import ispp.project.dondesiempre.modules.stores.models.StoreSocialNetwork;
import ispp.project.dondesiempre.modules.stores.models.Storefront;
import ispp.project.dondesiempre.modules.stores.repositories.SocialNetworkRepository;
import ispp.project.dondesiempre.modules.stores.repositories.StoreRepository;
import ispp.project.dondesiempre.modules.stores.repositories.StoreSocialNetworkRepository;
import ispp.project.dondesiempre.utils.cloudinary.CloudinaryService;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Profile("seed")
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

  private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);
  final SeedProperties props;
  final PasswordEncoder passwordEncoder;
  final UserRepository userRepository;
  final StoreRepository storeRepository;
  final SocialNetworkRepository socialNetworkRepository;
  final StoreSocialNetworkRepository storeSocialNetworkRepository;
  final ProductRepository productRepository;
  final ProductTypeRepository productTypeRepository;
  final ProductColorRepository productColorRepository;
  final ProductSizeRepository productSizeRepository;
  final ProductVariantRepository productVariantRepository;
  final OutfitRepository outfitRepository;
  final OutfitProductRepository outfitProductRepository;
  final OutfitTagRepository outfitTagRepository;
  final OutfitTagRelationRepository outfitTagRelationRepository;
  final ClientRepository clientRepository;
  final OrderRepository orderRepository;
  final StoreFollowerRepository storeFollowerRepository;
  final CloudinaryService cloudinaryService;
  final Cloudinary cloudinary;
  final CloudinaryProperties cloudinaryProperties;

  private static final GeometryFactory GF = new GeometryFactory(new PrecisionModel(), 4326);

  @Override
  public void run(String... args) {
    if (storeRepository.count() > 0) {
      log.info("Database already seeded, skipping.");
      return;
    }

    log.info("Seeding reference data...");
    seedReferenceData();
    log.info("Seeding database with store data...");
    loadStoreData();
    log.info("Seeding database with client data...");
    loadClientData();
    log.info("Database seeding complete.");
  }

  private void seedReferenceData() {
    if (socialNetworkRepository.count() == 0) {
      for (String name :
          List.of("Instagram", "Facebook", "TikTok", "X", "WhatsApp", "Web", "Teléfono")) {
        SocialNetwork sn = new SocialNetwork();
        sn.setName(name);
        socialNetworkRepository.save(sn);
      }
    }
    if (productTypeRepository.count() == 0) {
      for (String type :
          List.of("Camiseta", "Pantalón", "Vestido", "Chaqueta", "Zapatos", "Accesorio")) {
        ProductType pt = new ProductType();
        pt.setType(type);
        productTypeRepository.save(pt);
      }
    }
    if (productColorRepository.count() == 0) {
      for (String color :
          List.of("Negro", "Blanco", "Rojo", "Azul", "Verde", "Rosa", "Gris", "Beige")) {
        ProductColor pc = new ProductColor();
        pc.setColor(color);
        productColorRepository.save(pc);
      }
    }
    if (productSizeRepository.count() == 0) {
      for (String size : List.of("XS", "S", "M", "L", "XL", "XXL")) {
        ProductSize ps = new ProductSize();
        ps.setSize(size);
        productSizeRepository.save(ps);
      }
    }
    if (outfitTagRepository.count() == 0) {
      for (String name :
          List.of("Verano", "Invierno", "Casual", "Formal", "Deportivo", "Elegante")) {
        OutfitTag tag = new OutfitTag();
        tag.setName(name);
        outfitTagRepository.save(tag);
      }
    }
  }

  private void loadStoreData() {
    Map<String, SocialNetwork> socialNetworks =
        socialNetworkRepository.findAll().stream()
            .collect(Collectors.toMap(SocialNetwork::getName, sn -> sn));
    Map<String, ProductType> productTypes =
        productTypeRepository.findAll().stream()
            .collect(Collectors.toMap(ProductType::getType, pt -> pt));
    Map<String, ProductColor> productColors =
        productColorRepository.findAll().stream()
            .collect(Collectors.toMap(ProductColor::getColor, pc -> pc));
    Map<String, ProductSize> productSizes =
        productSizeRepository.findAll().stream()
            .collect(Collectors.toMap(ProductSize::getSize, ps -> ps));
    Map<String, OutfitTag> outfitTags =
        outfitTagRepository.findAll().stream()
            .collect(Collectors.toMap(OutfitTag::getName, ot -> ot));

    new GretaClosetStoreSeed(this)
        .seed(socialNetworks, productTypes, productColors, productSizes, outfitTags);
    new RomantikaStoreSeed(this)
        .seed(socialNetworks, productTypes, productColors, productSizes, outfitTags);
    new SanSebastianStoreSeed(this)
        .seed(socialNetworks, productTypes, productColors, productSizes, outfitTags);
    new RoireStoreSeed(this)
        .seed(socialNetworks, productTypes, productColors, productSizes, outfitTags);
    new PineappleStoreSeed(this)
        .seed(socialNetworks, productTypes, productColors, productSizes, outfitTags);
    new LucemesStoreSeed(this)
        .seed(socialNetworks, productTypes, productColors, productSizes, outfitTags);
    new AlfonsiStoreSeed(this)
        .seed(socialNetworks, productTypes, productColors, productSizes, outfitTags);
    new BazarRomeraStoreSeed(this)
        .seed(socialNetworks, productTypes, productColors, productSizes, outfitTags);
    new MarGovantesStoreSeed(this)
        .seed(socialNetworks, productTypes, productColors, productSizes, outfitTags);
  }

  private void loadClientData() {
    new ClientSeed(this).seed();
  }

  Store createStore(
      String name,
      String email,
      double lat,
      double lon,
      String address,
      String openingHours,
      String primaryColor,
      String secondaryColor,
      String bannerImageFilename,
      String aboutUs,
      String accountId) {

    User user = new User();
    user.setId(seedUuid("user:" + email));
    user.setEmail(email);
    user.setPassword(passwordEncoder.encode("Password123!"));
    userRepository.save(user);

    Storefront storefront = new Storefront();
    storefront.setPrimaryColor(primaryColor);
    storefront.setSecondaryColor(secondaryColor);

    String bannerUrl = uploadImage(bannerImageFilename);
    if (bannerUrl != null) {
      storefront.setBannerImageUrl(bannerUrl);
    }

    Point location = GF.createPoint(new Coordinate(lon, lat));

    Store store = new Store();
    store.setId(seedUuid("store:" + email));
    store.setName(name);
    store.setEmail(email);
    store.setLocation(location);
    store.setAddress(address);
    store.setOpeningHours(openingHours);
    store.setStorefront(storefront);
    store.setUser(user);
    store.setAboutUs(aboutUs);
    store.setAccountId(accountId);
    storeRepository.save(store);

    return store;
  }

  void addSocialNetwork(
      Store store, Map<String, SocialNetwork> socialNetworks, String type, String link) {
    StoreSocialNetwork ssn = new StoreSocialNetwork();
    ssn.setLink(link);
    ssn.setSocialNetwork(socialNetworks.get(type));
    ssn.setStore(store);
    storeSocialNetworkRepository.save(ssn);
  }

  Product createProduct(
      String name,
      int price,
      Integer discountedPrice,
      String description,
      ProductType type,
      Store store,
      String imageFilename) {
    Product product = new Product();
    product.setId(seedUuid("product:" + store.getId() + ":" + name));
    product.setName(name);
    product.setPriceInCents(price);
    product.setDiscountPercentage(discountedPrice);
    product.setDescription(description);
    product.setType(type);
    product.setStore(store);

    String imageUrl = uploadImage(imageFilename);
    if (imageUrl != null) {
      product.setImage(imageUrl);
    }

    return productRepository.save(product);
  }

  void createVariant(Product product, ProductSize size, ProductColor color, boolean isAvailable) {
    ProductVariant variant = new ProductVariant();
    variant.setProduct(product);
    variant.setSize(size);
    variant.setColor(color);
    variant.setIsAvailable(isAvailable);
    productVariantRepository.save(variant);
  }

  Outfit createOutfit(
      String name, int index, Integer discountPercentage, Store store, String imageFilename) {
    Outfit outfit = new Outfit();
    outfit.setId(seedUuid("outfit:" + store.getId() + ":" + name));
    outfit.setName(name);
    outfit.setIndex(index);
    outfit.setDiscountPercentage(discountPercentage);
    outfit.setStore(store);

    String imageUrl = uploadImage(imageFilename);
    if (imageUrl != null) {
      outfit.setImage(imageUrl);
    }

    return outfitRepository.save(outfit);
  }

  void createOutfitProduct(Outfit outfit, Product product, int index) {
    OutfitProduct op = new OutfitProduct();
    op.setOutfit(outfit);
    op.setProduct(product);
    op.setIndex(index);
    outfitProductRepository.save(op);
  }

  void createOutfitTagRelation(Outfit outfit, OutfitTag tag) {
    OutfitTagRelation rel = new OutfitTagRelation();
    rel.setOutfit(outfit);
    rel.setTag(tag);
    outfitTagRelationRepository.save(rel);
  }

  String uploadImage(String filename) {
    if (filename == null) return null;
    String nameOnly = filename.substring(filename.lastIndexOf('/') + 1);
    String publicId = nameOnly.replaceAll("\\.[^.]+$", "").replace(" ", "_");
    if (!props.isUploadImages()) {
      return "https://res.cloudinary.com/"
          + cloudinary.config.cloudName
          + "/image/upload/"
          + cloudinaryProperties.getFolderPrefix()
          + "/seed/"
          + publicId;
    }
    try {
      return cloudinaryService.uploadSeedResource("seed/images/" + filename, publicId);
    } catch (Exception e) {
      log.warn(
          "Failed to upload seed image '{}', continuing without it: {}", filename, e.getMessage());
      return null;
    }
  }

  <T> T pick(List<T> list, Random rng) {
    return list.get(rng.nextInt(list.size()));
  }

  void addItemsToOrder(Order order, List<Product> products) {
    int total = 0;

    List<ProductVariant> allVariants = productVariantRepository.findAll();

    for (Product p : products) {
      ProductVariant variant =
          allVariants.stream()
              .filter(v -> v.getProduct().getId().equals(p.getId()))
              .findFirst()
              .orElse(null);

      if (variant == null) {
        log.warn(
            "IGNORANDO PRODUCTO: '{}' (ID: {}) no tiene variantes. "
                + "No se puede añadir al pedido debido a restricciones de integridad.",
            p.getName(),
            p.getId());
        continue; // Salta al siguiente producto del bucle
      }

      OrderItem item = new OrderItem();
      item.setOrder(order);
      item.setProduct(p);
      item.setQuantity(1);
      item.setPriceAtPurchase(p.getPriceInCents());

      item.setVariant(variant);

      if (order.getItems() == null) {
        order.setItems(new java.util.ArrayList<>());
      }
      order.getItems().add(item);
      total += p.getPriceInCents();
    }

    order.setTotalPrice(total);
  }

  List<String> loadTextFile(String path) {
    InputStream is = getClass().getClassLoader().getResourceAsStream(path);
    if (is == null) {
      throw new IllegalStateException("Seed file not found: " + path);
    }
    try (BufferedReader reader =
        new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
      return reader
          .lines()
          .map(
              line -> {
                int commentIdx = line.indexOf('#');
                return commentIdx >= 0 ? line.substring(0, commentIdx) : line;
              })
          .map(String::trim)
          .filter(line -> !line.isEmpty())
          .collect(Collectors.toList());
    } catch (IOException e) {
      throw new IllegalStateException("Failed to read seed file: " + path, e);
    }
  }

  UUID seedUuid(String name) {
    return UUID.nameUUIDFromBytes(
        ("seed:" + props.getRandomSeed() + ":" + name).getBytes(StandardCharsets.UTF_8));
  }
}
