package ispp.project.dondesiempre.seed;

import ispp.project.dondesiempre.models.Client;
import ispp.project.dondesiempre.models.User;
import ispp.project.dondesiempre.models.outfits.Outfit;
import ispp.project.dondesiempre.models.outfits.OutfitProduct;
import ispp.project.dondesiempre.models.outfits.OutfitTag;
import ispp.project.dondesiempre.models.outfits.OutfitTagRelation;
import ispp.project.dondesiempre.models.products.Product;
import ispp.project.dondesiempre.models.products.ProductColor;
import ispp.project.dondesiempre.models.products.ProductSize;
import ispp.project.dondesiempre.models.products.ProductType;
import ispp.project.dondesiempre.models.products.ProductVariant;
import ispp.project.dondesiempre.models.storefronts.Storefront;
import ispp.project.dondesiempre.models.stores.SocialNetwork;
import ispp.project.dondesiempre.models.stores.Store;
import ispp.project.dondesiempre.models.stores.StoreSocialNetwork;
import ispp.project.dondesiempre.repositories.ClientRepository;
import ispp.project.dondesiempre.repositories.UserRepository;
import ispp.project.dondesiempre.repositories.outfits.OutfitProductRepository;
import ispp.project.dondesiempre.repositories.outfits.OutfitRepository;
import ispp.project.dondesiempre.repositories.outfits.OutfitTagRelationRepository;
import ispp.project.dondesiempre.repositories.outfits.OutfitTagRepository;
import ispp.project.dondesiempre.repositories.products.ProductColorRepository;
import ispp.project.dondesiempre.repositories.products.ProductRepository;
import ispp.project.dondesiempre.repositories.products.ProductSizeRepository;
import ispp.project.dondesiempre.repositories.products.ProductTypeRepository;
import ispp.project.dondesiempre.repositories.products.ProductVariantRepository;
import ispp.project.dondesiempre.repositories.stores.SocialNetworkRepository;
import ispp.project.dondesiempre.repositories.stores.StoreRepository;
import ispp.project.dondesiempre.repositories.stores.StoreSocialNetworkRepository;
import ispp.project.dondesiempre.services.UserService;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
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
import org.springframework.stereotype.Component;

@Component
@Profile("seed")
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

  private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

  private final SeedProperties props;
  private final UserRepository userRepository;
  private final StoreRepository storeRepository;
  private final SocialNetworkRepository socialNetworkRepository;
  private final StoreSocialNetworkRepository storeSocialNetworkRepository;
  private final ProductRepository productRepository;
  private final ProductTypeRepository productTypeRepository;
  private final ProductColorRepository productColorRepository;
  private final ProductSizeRepository productSizeRepository;
  private final ProductVariantRepository productVariantRepository;
  private final OutfitRepository outfitRepository;
  private final OutfitProductRepository outfitProductRepository;
  private final OutfitTagRepository outfitTagRepository;
  private final OutfitTagRelationRepository outfitTagRelationRepository;
  private final ClientRepository clientRepository;

  private static final GeometryFactory GF = new GeometryFactory(new PrecisionModel(), 4326);

  @Override
  public void run(String... args) {
    if (storeRepository.count() > 0) {
      log.info("Database already seeded, skipping.");
      return;
    }

    log.info("Seeding database with manual example data...");
    loadManualData();
    log.info("Seeding database with random data...");
    loadRandomData();
    log.info("Database seeding complete.");
  }

  private void loadManualData() {
    // Fetch reference data
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

    // Create user that owns the manual store (this is the seed user for getCurrentUser())
    User storeOwner = new User();
    storeOwner.setEmail(UserService.SEED_USER_EMAIL);
    storeOwner.setPassword("password123");
    userRepository.save(storeOwner);

    // Create storefront (saved via CascadeType.ALL on Store.storefront)
    Storefront storefront = new Storefront();
    storefront.setPrimaryColor("#c65a3a");
    storefront.setSecondaryColor("#19756a");
    storefront.setIsFirstCollections(true);

    // Create store
    Store store = new Store();
    store.setName("La Boutique de Sevilla");
    store.setEmail("laboutique@ejemplo.es");
    store.setStoreID("TIENDA-MAN-001");
    store.setLocation(GF.createPoint(new Coordinate(-5.923503017051423, 37.28749765023422)));
    store.setAddress("Calle Sierpes, nº 45, Sevilla");
    store.setOpeningHours("Lun-Sáb: 10:00-21:00");
    store.setPhone("+34 612345678");
    store.setAboutUs(
        "Tienda de moda local con más de 10 años de experiencia en el sector textil sevillano.");
    store.setAcceptsShipping(true);
    store.setStorefront(storefront);
    store.setUser(storeOwner);
    storeRepository.save(store);

    // Social networks
    StoreSocialNetwork ssn1 = new StoreSocialNetwork();
    ssn1.setLink("https://www.instagram.com/laboutique_sevilla");
    ssn1.setSocialNetwork(socialNetworks.get("Instagram"));
    ssn1.setStore(store);
    storeSocialNetworkRepository.save(ssn1);

    StoreSocialNetwork ssn2 = new StoreSocialNetwork();
    ssn2.setLink("https://www.facebook.com/laboutiquesevilla");
    ssn2.setSocialNetwork(socialNetworks.get("Facebook"));
    ssn2.setStore(store);
    storeSocialNetworkRepository.save(ssn2);

    // Products
    Product p1 =
        createProduct(
            "Camiseta Lino Mediterráneo",
            2999,
            2999,
            "Camiseta de lino natural de verano.",
            productTypes.get("Camiseta"),
            store);
    Product p2 =
        createProduct(
            "Pantalón Palazzo Verano",
            4999,
            3999,
            "Pantalón fluido perfecto para el verano.",
            productTypes.get("Pantalón"),
            store);
    Product p3 =
        createProduct(
            "Vestido Midi Floral",
            6999,
            6999,
            "Vestido midi con estampado floral primaveral.",
            productTypes.get("Vestido"),
            store);
    Product p4 =
        createProduct(
            "Chaqueta Punto Artesanal",
            8999,
            7499,
            "Chaqueta de punto tejida a mano.",
            productTypes.get("Chaqueta"),
            store);

    // Product variants
    createVariant(p1, productSizes.get("S"), productColors.get("Blanco"), true);
    createVariant(p1, productSizes.get("M"), productColors.get("Blanco"), true);
    createVariant(p1, productSizes.get("L"), productColors.get("Beige"), false);
    createVariant(p2, productSizes.get("S"), productColors.get("Negro"), true);
    createVariant(p2, productSizes.get("M"), productColors.get("Azul"), true);
    createVariant(p3, productSizes.get("M"), productColors.get("Rosa"), true);
    createVariant(p3, productSizes.get("L"), productColors.get("Verde"), false);
    createVariant(p4, productSizes.get("S"), productColors.get("Gris"), true);
    createVariant(p4, productSizes.get("M"), productColors.get("Beige"), true);

    // Outfits
    Outfit outfit1 = createOutfit("Look Verano Andaluz", 0, store.getStorefront());
    createOutfitProduct(outfit1, p1, 0);
    createOutfitProduct(outfit1, p2, 1);
    createOutfitTagRelation(outfit1, outfitTags.get("Verano"));

    Outfit outfit2 = createOutfit("Estilo Mediterráneo", 1, store.getStorefront());
    createOutfitProduct(outfit2, p3, 0);
    createOutfitProduct(outfit2, p4, 1);
    createOutfitTagRelation(outfit2, outfitTags.get("Elegante"));

    // Client
    User clientUser = new User();
    clientUser.setEmail("ana.garcia@ejemplo.es");
    clientUser.setPassword("password123");
    userRepository.save(clientUser);

    Client client = new Client();
    client.setName("Ana");
    client.setSurname("García");
    client.setEmail("ana.garcia@ejemplo.es");
    client.setPhone("+34 623456789");
    client.setAddress("Calle San Fernando, nº 12, Sevilla");
    client.setUser(clientUser);
    clientRepository.save(client);
  }

  private void loadRandomData() {
    Random rng = new Random(props.getRandomSeed());

    List<String> phoneNumbers = loadTextFile("seed/phone-numbers.txt");
    List<String> coordinates = loadTextFile("seed/coordinates.txt");
    List<String> hexColors = loadTextFile("seed/hex-colors.txt");
    List<String> storeNames = loadTextFile("seed/store-names.txt");
    List<String> addresses = loadTextFile("seed/addresses.txt");
    List<String> openingHours = loadTextFile("seed/opening-hours.txt");
    List<String> aboutUsList = loadTextFile("seed/about-us.txt");
    List<String> productNames = loadTextFile("seed/product-names.txt");
    List<String> outfitNames = loadTextFile("seed/outfit-names.txt");
    List<String> firstNames = loadTextFile("seed/client-first-names.txt");
    List<String> surnames = loadTextFile("seed/client-surnames.txt");

    List<SocialNetwork> allSocialNetworks = socialNetworkRepository.findAll();
    List<ProductType> allProductTypes = productTypeRepository.findAll();
    List<ProductColor> allProductColors = productColorRepository.findAll();
    List<ProductSize> allProductSizes = productSizeRepository.findAll();
    List<OutfitTag> allOutfitTags = outfitTagRepository.findAll();

    for (int i = 0; i < props.getStoreCount(); i++) {
      String storeEmail = "tienda" + i + "@ejemplo.es";

      User storeUser = new User();
      storeUser.setEmail(storeEmail);
      storeUser.setPassword("password123");
      userRepository.save(storeUser);

      Storefront storefront = new Storefront();
      storefront.setPrimaryColor("#" + pick(hexColors, rng));
      storefront.setSecondaryColor("#" + pick(hexColors, rng));
      storefront.setIsFirstCollections(rng.nextBoolean());

      String[] latLon = pick(coordinates, rng).split(",");
      double lat = Double.parseDouble(latLon[0].trim());
      double lon = Double.parseDouble(latLon[1].trim());
      Point location = GF.createPoint(new Coordinate(lon, lat));

      Store store = new Store();
      store.setName(pick(storeNames, rng));
      store.setEmail(storeEmail);
      store.setStoreID(String.format("TIENDA-RND-%03d", i));
      store.setLocation(location);
      store.setAddress(pick(addresses, rng));
      store.setOpeningHours(pick(openingHours, rng));
      store.setPhone(pick(phoneNumbers, rng));
      store.setAboutUs(pick(aboutUsList, rng));
      store.setAcceptsShipping(rng.nextBoolean());
      store.setStorefront(storefront);
      store.setUser(storeUser);
      storeRepository.save(store);

      // Social networks
      List<SocialNetwork> shuffledNetworks = new ArrayList<>(allSocialNetworks);
      for (int j = 0; j < props.getSocialNetworksPerStore() && j < shuffledNetworks.size(); j++) {
        int idx = j + rng.nextInt(shuffledNetworks.size() - j);
        SocialNetwork sn = shuffledNetworks.get(idx);
        shuffledNetworks.set(idx, shuffledNetworks.get(j));
        shuffledNetworks.set(j, sn);

        StoreSocialNetwork ssn = new StoreSocialNetwork();
        ssn.setLink("https://www." + sn.getName().toLowerCase() + ".com/tienda" + i);
        ssn.setSocialNetwork(sn);
        ssn.setStore(store);
        storeSocialNetworkRepository.save(ssn);
      }

      // Products
      List<Product> storeProducts = new ArrayList<>();
      for (int j = 0; j < props.getProductsPerStore(); j++) {
        int price = (rng.nextInt(200) + 10) * 100;
        boolean hasDiscount = rng.nextBoolean();
        int discountedPrice = hasDiscount ? (int) (price * (0.6 + rng.nextDouble() * 0.35)) : price;

        Product product =
            createProduct(
                pick(productNames, rng),
                price,
                discountedPrice,
                null,
                pick(allProductTypes, rng),
                store);
        storeProducts.add(product);

        // 1-3 variants per product
        int variantCount = 1 + rng.nextInt(3);
        for (int k = 0; k < variantCount; k++) {
          createVariant(
              product, pick(allProductSizes, rng), pick(allProductColors, rng), rng.nextBoolean());
        }
      }

      // Outfits
      for (int j = 0; j < props.getOutfitsPerStore(); j++) {
        Outfit outfit = createOutfit(pick(outfitNames, rng), j, store.getStorefront());

        int productsInOutfit = 2 + rng.nextInt(2);
        for (int k = 0; k < productsInOutfit && k < storeProducts.size(); k++) {
          createOutfitProduct(outfit, storeProducts.get(k), k);
        }
        createOutfitTagRelation(outfit, pick(allOutfitTags, rng));
      }
    }

    // Clients
    for (int i = 0; i < props.getClientCount(); i++) {
      String name = pick(firstNames, rng);
      String surname = pick(surnames, rng);
      String normalizedName =
          name.toLowerCase()
              .replace("á", "a")
              .replace("é", "e")
              .replace("í", "i")
              .replace("ó", "o")
              .replace("ú", "u")
              .replace("ñ", "n");
      String normalizedSurname =
          surname
              .toLowerCase()
              .replace("á", "a")
              .replace("é", "e")
              .replace("í", "i")
              .replace("ó", "o")
              .replace("ú", "u")
              .replace("ñ", "n");

      String clientEmail = normalizedName + "." + normalizedSurname + i + "@ejemplo.es";

      User clientUser = new User();
      clientUser.setEmail(clientEmail);
      clientUser.setPassword("password123");
      userRepository.save(clientUser);

      Client client = new Client();
      client.setName(name);
      client.setSurname(surname);
      client.setEmail(clientEmail);
      client.setPhone(pick(phoneNumbers, rng));
      client.setAddress(pick(addresses, rng));
      client.setUser(clientUser);
      clientRepository.save(client);
    }
  }

  private Product createProduct(
      String name,
      int price,
      int discountedPrice,
      String description,
      ProductType type,
      Store store) {
    Product product = new Product();
    product.setName(name);
    product.setPriceInCents(price);
    product.setDiscountedPriceInCents(discountedPrice);
    product.setDescription(description);
    product.setType(type);
    product.setStore(store);
    return productRepository.save(product);
  }

  private void createVariant(
      Product product, ProductSize size, ProductColor color, boolean isAvailable) {
    ProductVariant variant = new ProductVariant();
    variant.setProduct(product);
    variant.setSize(size);
    variant.setColor(color);
    variant.setIsAvailable(isAvailable);
    productVariantRepository.save(variant);
  }

  private Outfit createOutfit(String name, int index, Storefront storefront) {
    Outfit outfit = new Outfit();
    outfit.setName(name);
    outfit.setIndex(index);
    outfit.setDiscountedPriceInCents(0);
    outfit.setStorefront(storefront);
    return outfitRepository.save(outfit);
  }

  private void createOutfitProduct(Outfit outfit, Product product, int index) {
    OutfitProduct op = new OutfitProduct();
    op.setOutfit(outfit);
    op.setProduct(product);
    op.setIndex(index);
    outfitProductRepository.save(op);
  }

  private void createOutfitTagRelation(Outfit outfit, OutfitTag tag) {
    OutfitTagRelation rel = new OutfitTagRelation();
    rel.setOutfit(outfit);
    rel.setTag(tag);
    outfitTagRelationRepository.save(rel);
  }

  private <T> T pick(List<T> list, Random rng) {
    return list.get(rng.nextInt(list.size()));
  }

  private List<String> loadTextFile(String path) {
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
}
