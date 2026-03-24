package ispp.project.dondesiempre.seed;

import com.cloudinary.Cloudinary;
import ispp.project.dondesiempre.config.CloudinaryProperties;
import ispp.project.dondesiempre.modules.auth.models.User;
import ispp.project.dondesiempre.modules.auth.repositories.UserRepository;
import ispp.project.dondesiempre.modules.clients.models.Client;
import ispp.project.dondesiempre.modules.clients.repositories.ClientRepository;
import ispp.project.dondesiempre.modules.follows.models.StoreFollower;
import ispp.project.dondesiempre.modules.follows.repositories.StoreFollowerRepository;
import ispp.project.dondesiempre.modules.orders.models.Order;
import ispp.project.dondesiempre.modules.orders.models.OrderItem;
import ispp.project.dondesiempre.modules.orders.models.OrderStatus;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
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
  private final SeedProperties props;
  private final PasswordEncoder passwordEncoder;
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
  private final OrderRepository orderRepository;
  private final StoreFollowerRepository storeFollowerRepository;
  private final CloudinaryService cloudinaryService;
  private final Cloudinary cloudinary;
  private final CloudinaryProperties cloudinaryProperties;

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

    // ── 1. Greta Closet ──────────────────────────────────────────────────────
    Store greta =
        createStore(
            "Greta Closet",
            "demo@gretacloset.com",
            37.283160057885304,
            -5.9237761491318235,
            "C. Sta. María Magdalena, 14, 41701 Dos Hermanas, Sevilla",
            "Lun-Vie 10:00-13:00, 17:30-20:30. Sab 10:30-14:00",
            "#000000",
            "#a1005c",
            "escaparate_greta_closet.png");
    addSocialNetwork(greta, socialNetworks, "Instagram", "https://www.instagram.com/gretacloset/");
    addSocialNetwork(
        greta, socialNetworks, "TikTok", "https://www.tiktok.com/@gretaclosetcomplementos");
    addSocialNetwork(greta, socialNetworks, "WhatsApp", "https://wa.me/34694466991");
    addSocialNetwork(greta, socialNetworks, "Web", "https://gretacloset.com/");
    addSocialNetwork(greta, socialNetworks, "Teléfono", "tel:+34694466991");

    Product greta_vestidoBlanco =
        createProduct(
            "Vestido Blanco",
            4999,
            null,
            "Vestido blanco, perfecto para los días de calor.",
            productTypes.get("Vestido"),
            greta,
            "outfit1_vestido_blanco_49.99.jpg");
    createVariant(greta_vestidoBlanco, productSizes.get("M"), productColors.get("Blanco"), true);

    Product greta_botasNegras1 =
        createProduct(
            "Botas Negras",
            6999,
            15,
            "Botas negras, ideales para los meses de frío.",
            productTypes.get("Zapatos"),
            greta,
            "outfit1_botas_negras_69.99.jpg");
    createVariant(greta_botasNegras1, productSizes.get("M"), productColors.get("Negro"), true);

    Product greta_bolsoMarron =
        createProduct(
            "Bolso Marrón",
            3499,
            null,
            "Bolso marrón de estilo moderno, perfecto para el día a día.",
            productTypes.get("Accesorio"),
            greta,
            "outfit1_bolso_marron_34.99.jpg");
    createVariant(greta_bolsoMarron, productSizes.get("M"), productColors.get("Beige"), true);

    Product greta_vestidoAzul =
        createProduct(
            "Vestido Azul",
            6999,
            20,
            "Vestido azul, perfecto para una tarde de verano.",
            productTypes.get("Vestido"),
            greta,
            "outfit2_vestido_azul_69.99.jpg");
    createVariant(greta_vestidoAzul, productSizes.get("M"), productColors.get("Azul"), true);

    Product greta_bolsoBeige =
        createProduct(
            "Bolso Beige",
            4999,
            null,
            "Bolso beige de estilo moderno, ideal para el día a día.",
            productTypes.get("Accesorio"),
            greta,
            "outfit2_bolso_beige_49.99.jpg");
    createVariant(greta_bolsoBeige, productSizes.get("M"), productColors.get("Beige"), true);

    Product greta_pendientes =
        createProduct(
            "Pendientes Mariposa Oro",
            1999,
            10,
            "Pendientes mariposa dorados, un toque elegante para cualquier look.",
            productTypes.get("Accesorio"),
            greta,
            "outfit2_pendientes_mariposa_oro_19.99.jpg");
    createVariant(greta_pendientes, productSizes.get("S"), productColors.get("Beige"), true);

    Outfit greta_outfit1 =
        createOutfit("Conjunto Mihai", 0, null, greta, "outfit1_conjunto_mihai.jpg");
    createOutfitTagRelation(greta_outfit1, outfitTags.get("Elegante"));
    createOutfitProduct(greta_outfit1, greta_vestidoBlanco, 0);
    createOutfitProduct(greta_outfit1, greta_botasNegras1, 1);
    createOutfitProduct(greta_outfit1, greta_bolsoMarron, 2);

    Outfit greta_outfit2 =
        createOutfit("Conjunto Galilea", 1, 25, greta, "outfit2_conjunto_galilea.jpg");
    createOutfitTagRelation(greta_outfit2, outfitTags.get("Casual"));
    createOutfitProduct(greta_outfit2, greta_vestidoAzul, 0);
    createOutfitProduct(greta_outfit2, greta_bolsoBeige, 1);
    createOutfitProduct(greta_outfit2, greta_pendientes, 2);

    // ── 2. Modas Romantika Vintage ────────────────────────────────────────────
    Store romantika =
        createStore(
            "Modas Romantika Vintage",
            "demo@romantikavintage.es",
            37.280502359992376,
            -5.920509768052806,
            "C. San Sebastián, 17, 41701 Dos Hermanas, Sevilla",
            "Lun-Vie 10:30-13:30, 17:30-20:30. Sab 10:30-13:30",
            "#315750",
            "#2b4f39",
            "escaparate_romantika.png");
    addSocialNetwork(romantika, socialNetworks, "WhatsApp", "https://wa.me/34645142782");
    addSocialNetwork(
        romantika, socialNetworks, "Instagram", "https://www.instagram.com/romantikavintage/");
    addSocialNetwork(
        romantika, socialNetworks, "Facebook", "https://www.facebook.com/RomantikaVintage/");
    addSocialNetwork(romantika, socialNetworks, "Web", "http://www.romantikavintage.es/");
    addSocialNetwork(romantika, socialNetworks, "Teléfono", "tel:+34955668820");

    Product rom_vestidoRojo =
        createProduct(
            "Vestido Rojo",
            6499,
            null,
            "Vestido rojo, ideal para una ocasión especial en verano.",
            productTypes.get("Vestido"),
            romantika,
            "outfit3_vestido_rojo_64.99.jpg");
    createVariant(rom_vestidoRojo, productSizes.get("M"), productColors.get("Rojo"), true);

    Product rom_sandaliasRojas =
        createProduct(
            "Sandalias Rojas con Tacón",
            4699,
            25,
            "Sandalias rojas con tacón, perfectas para el buen tiempo.",
            productTypes.get("Zapatos"),
            romantika,
            "outfit3_sandalias_rojas_con_tacón_46.99.jpg");
    createVariant(rom_sandaliasRojas, productSizes.get("M"), productColors.get("Rojo"), true);

    Product rom_vestidoVerde =
        createProduct(
            "Vestido Verde",
            4999,
            null,
            "Vestido verde, una opción fresca para la primavera.",
            productTypes.get("Vestido"),
            romantika,
            "outfit4_vestido_verde_49.99.jpg");
    createVariant(rom_vestidoVerde, productSizes.get("M"), productColors.get("Verde"), true);

    Product rom_taconesBeige =
        createProduct(
            "Tacones Beige",
            4499,
            10,
            "Tacones beige, versátiles para cualquier ocasión.",
            productTypes.get("Zapatos"),
            romantika,
            "outfit4_tacones_beige_44.99.jpg");
    createVariant(rom_taconesBeige, productSizes.get("M"), productColors.get("Beige"), true);

    Outfit rom_outfit1 = createOutfit("Verano Rojo", 0, 10, romantika, "outfit3_verano_rojo.jpg");
    createOutfitTagRelation(rom_outfit1, outfitTags.get("Verano"));
    createOutfitProduct(rom_outfit1, rom_vestidoRojo, 0);
    createOutfitProduct(rom_outfit1, rom_sandaliasRojas, 1);

    Outfit rom_outfit2 =
        createOutfit("Primavera Verde", 1, 15, romantika, "outfit4_primavera_verde.jpg");
    createOutfitTagRelation(rom_outfit2, outfitTags.get("Verano"));
    createOutfitProduct(rom_outfit2, rom_vestidoVerde, 0);
    createOutfitProduct(rom_outfit2, rom_taconesBeige, 1);

    // ── 3. Confecciones y Hogar San Sebastián ─────────────────────────────────
    Store sanSebastian =
        createStore(
            "Confecciones y Hogar San Sebastián",
            "demo@confeccionesyhogarsansebastian.com",
            37.27961006469284,
            -5.920175962489425,
            "C/ San Sebastián, 35, 41701 Dos Hermanas, Sevilla",
            "Lun-Vie 9:45-21:00, Sab 9:30-13:30",
            "#000000",
            "#ab327d",
            "escaparate_san_sebastian.png");
    addSocialNetwork(
        sanSebastian,
        socialNetworks,
        "Instagram",
        "https://www.instagram.com/tejidossansebastian/");
    addSocialNetwork(sanSebastian, socialNetworks, "WhatsApp", "https://wa.me/34691537089");
    addSocialNetwork(
        sanSebastian, socialNetworks, "Web", "https://www.confeccionesyhogarsansebastian.com/");
    addSocialNetwork(sanSebastian, socialNetworks, "Teléfono", "tel:+34691537089");

    Product ss_albornoz =
        createProduct(
            "Albornoz Blanco",
            1999,
            null,
            "Albornoz blanco, perfecto para después del baño o la playa.",
            productTypes.get("Accesorio"),
            sanSebastian,
            "outfit5_albornoz_blanco_19.99.jpg");
    createVariant(ss_albornoz, productSizes.get("M"), productColors.get("Blanco"), true);

    Product ss_crocs =
        createProduct(
            "Crocs Negras",
            1599,
            15,
            "Crocs negras, cómodas para el uso diario en casa o en la calle.",
            productTypes.get("Zapatos"),
            sanSebastian,
            "outfit5_crocs_negras_15.99.jpg");
    createVariant(ss_crocs, productSizes.get("M"), productColors.get("Negro"), true);

    Outfit ss_outfit1 =
        createOutfit(
            "Tranquilidad Casera", 0, null, sanSebastian, "outfit5_tranquilidad_casera.jpg");
    createOutfitTagRelation(ss_outfit1, outfitTags.get("Casual"));
    createOutfitProduct(ss_outfit1, ss_albornoz, 0);
    createOutfitProduct(ss_outfit1, ss_crocs, 1);

    // ── 4. Roire ──────────────────────────────────────────────────────────────
    Store roire =
        createStore(
            "Roire",
            "demo@roire.es",
            37.280633989355685,
            -5.920593858571272,
            "C. San Sebastián, 15, 41701 Dos Hermanas, Sevilla",
            "Lun-Vie 10:00-13:45, 17:30-21:00, Sábado 10:00-14:00",
            "#ad0000",
            "#633a00",
            "escaparate_roire.png");
    addSocialNetwork(roire, socialNetworks, "WhatsApp", "https://wa.me/34641231378");
    addSocialNetwork(roire, socialNetworks, "Instagram", "https://www.instagram.com/roire.es");
    addSocialNetwork(roire, socialNetworks, "Web", "https://tiendaroire.es/");
    addSocialNetwork(roire, socialNetworks, "Teléfono", "tel:+34641231378");

    Product roire_faldaCebra =
        createProduct(
            "Falda Cebra",
            2999,
            20,
            "Falda cebra con estilo, ideal para un look casual llamativo.",
            productTypes.get("Pantalón"),
            roire,
            "outfit6_falda_cebra_29.99.jpg");
    createVariant(roire_faldaCebra, productSizes.get("M"), productColors.get("Blanco"), true);

    Product roire_sueterMarron =
        createProduct(
            "Suéter Marrón",
            4999,
            null,
            "Suéter marrón, muy abrigado para los días de frío.",
            productTypes.get("Chaqueta"),
            roire,
            "outfit6_suéter_marrón_49.99.jpg");
    createVariant(roire_sueterMarron, productSizes.get("M"), productColors.get("Beige"), true);

    Outfit roire_outfit1 = createOutfit("Savana Otoñal", 0, 20, roire, "outfit6_savana_otoñal.jpg");
    createOutfitTagRelation(roire_outfit1, outfitTags.get("Casual"));
    createOutfitProduct(roire_outfit1, roire_faldaCebra, 0);
    createOutfitProduct(roire_outfit1, roire_sueterMarron, 1);

    // ── 5. Pineapple Moda ─────────────────────────────────────────────────────
    Store pineapple =
        createStore(
            "Pineapple Moda",
            "demo@pineapplemoda.com",
            37.28212759058594,
            -5.921767187403529,
            "C. Canónigo, 73, 41701 Dos Hermanas, Sevilla",
            "Lun-Vie 10:30-13:30, 17:30-20:30, Sab 10:30-13:30",
            "#75502b",
            "#333333",
            "escaparate_pineapple.png");
    addSocialNetwork(pineapple, socialNetworks, "WhatsApp", "https://wa.me/34644807498");
    addSocialNetwork(
        pineapple,
        socialNetworks,
        "Facebook",
        "https://www.facebook.com/p/Pineapple-Moda-100070328611143/");
    addSocialNetwork(
        pineapple, socialNetworks, "Instagram", "https://www.instagram.com/pineapple.sevilla/");
    addSocialNetwork(
        pineapple, socialNetworks, "TikTok", "https://www.tiktok.com/@pineapplesevilla");
    addSocialNetwork(pineapple, socialNetworks, "Web", "https://pineapplemoda.com/");
    addSocialNetwork(pineapple, socialNetworks, "Teléfono", "tel:+34644807498");

    Product pine_camisaBlanca =
        createProduct(
            "Camisa Blanca",
            1599,
            null,
            "Camisa blanca, un básico de armario para cualquier época del año.",
            productTypes.get("Camiseta"),
            pineapple,
            "outfit7_camisa_blanca_15.99.jpg");
    createVariant(pine_camisaBlanca, productSizes.get("M"), productColors.get("Blanco"), true);

    Product pine_faldaRoja =
        createProduct(
            "Falda Roja",
            4599,
            10,
            "Falda roja, perfecta para darle color al otoño.",
            productTypes.get("Pantalón"),
            pineapple,
            "outfit7_falda_roja_45.99.jpg");
    createVariant(pine_faldaRoja, productSizes.get("M"), productColors.get("Rojo"), true);

    Outfit pine_outfit1 =
        createOutfit("Pasión de Otoño", 0, 30, pineapple, "outfit7_pasión_de_otoño.jpg");
    createOutfitTagRelation(pine_outfit1, outfitTags.get("Casual"));
    createOutfitProduct(pine_outfit1, pine_camisaBlanca, 0);
    createOutfitProduct(pine_outfit1, pine_faldaRoja, 1);

    // ── 6. Luceme's Bags ──────────────────────────────────────────────────────
    Store lucemes =
        createStore(
            "Luceme's Bags",
            "demo@lucemesbags.com",
            37.28090292017098,
            -5.9208650271174506,
            "C/ San Sebastián, 6 - Dos Hermanas",
            "Horarios sin confirmar",
            "#a88743",
            "#a84843",
            "escaparate_lucemes_bags.png");
    addSocialNetwork(
        lucemes,
        socialNetworks,
        "Facebook",
        "https://www.facebook.com/profile.php?id=100090603545882");
    createProduct(
        "Bolso Crema",
        1599,
        null,
        "Bolso crema de estilo moderno, ideal para cualquier ocasión.",
        productTypes.get("Accesorio"),
        lucemes,
        "producto_bolso_crema_15.99.jpg");
    createProduct(
        "Bolso Crema Oro",
        1899,
        null,
        "Bolso crema con detalles dorados, un complemento elegante y atemporal.",
        productTypes.get("Accesorio"),
        lucemes,
        "producto_bolso_crema_oro_18.99.jpg");

    // ── 7. Confecciones Alfonsi ───────────────────────────────────────────────
    Store alfonsi =
        createStore(
            "Confecciones Alfonsi",
            "demo@alfonsi.com",
            37.28073691177687,
            -5.922528642533732,
            "Calle Romera, 32, 41701 Dos Hermanas, Sevilla",
            "Horarios sin confirmar",
            "#183b27",
            "#214a1b",
            "escaparate_alfonsi.png");
    addSocialNetwork(
        alfonsi, socialNetworks, "Facebook", "https://www.facebook.com/confecciones.alfonsi/");

    Product alf_parkaBlanca =
        createProduct(
            "Parka Blanca",
            7999,
            30,
            "Parka blanca, ideal para afrontar el invierno con estilo.",
            productTypes.get("Chaqueta"),
            alfonsi,
            "outfit8_parka_blanca_79.99.jpg");
    createVariant(alf_parkaBlanca, productSizes.get("M"), productColors.get("Blanco"), true);

    Product alf_pantalonesNegros =
        createProduct(
            "Pantalones Negros",
            4699,
            null,
            "Pantalones negros, un básico imprescindible para cualquier temporada.",
            productTypes.get("Pantalón"),
            alfonsi,
            "outfit8_pantalones_negros_46.99.jpg");
    createVariant(alf_pantalonesNegros, productSizes.get("M"), productColors.get("Negro"), true);

    Product alf_botasNegras =
        createProduct(
            "Botas Negras",
            8999,
            null,
            "Botas negras, perfectas para los días fríos de invierno.",
            productTypes.get("Zapatos"),
            alfonsi,
            "outfit8_botas_negras_89.99.jpg");
    createVariant(alf_botasNegras, productSizes.get("M"), productColors.get("Negro"), true);

    Outfit alf_outfit1 =
        createOutfit("Cómodo Invierno", 0, null, alfonsi, "outfit8_cómodo_invierno.jpg");
    createOutfitTagRelation(alf_outfit1, outfitTags.get("Invierno"));
    createOutfitProduct(alf_outfit1, alf_parkaBlanca, 0);
    createOutfitProduct(alf_outfit1, alf_pantalonesNegros, 1);
    createOutfitProduct(alf_outfit1, alf_botasNegras, 2);

    // ── 8. Bazar Romera ───────────────────────────────────────────────────────
    Store bazarRomera =
        createStore(
            "Bazar Romera",
            "demo@bazarromera.com",
            37.28156704784245,
            -5.921647213639721,
            "C. Romera, 8, 41701 Dos Hermanas, Sevilla",
            "Lun-Vie: 9:00-14:00, 17:00-21:00. Sab 9:00-14:00",
            "#255d56",
            "#247054",
            "escaparate_bazar_romera.png");
    addSocialNetwork(bazarRomera, socialNetworks, "Teléfono", "tel:+34631122308");

    createProduct(
        "Camisa Azul",
        3499,
        null,
        "Camisa azul, un básico cómodo para el día a día.",
        productTypes.get("Camiseta"),
        bazarRomera,
        "producto_camisa_azul_34.99.jpg");
    createProduct(
        "Chandal Negro",
        2999,
        20,
        "Chándal negro, cómodo y apropiado para el día a día.",
        productTypes.get("Pantalón"),
        bazarRomera,
        "producto_chandal_negro_29.99.jpg");

    // ── 9. Mar Govantes ───────────────────────────────────────────────────────
    Store marGovantes =
        createStore(
            "Mar Govantes",
            "demo.margovantes@gmail.com",
            37.282795534740714,
            -5.924663169087747,
            "41701 Dos Hermanas, Sevilla",
            "Lun,Mar,Jue,Vie: 9:30-14:00. Mie: 9:30-14:00, 5:30-8:30. Sab 10:00-13:30",
            "#000045",
            "#844652",
            "escaparate_mar_govantes.jpg");
    addSocialNetwork(
        marGovantes, socialNetworks, "Facebook", "https://www.facebook.com/margovantesmodas/");
    addSocialNetwork(
        marGovantes, socialNetworks, "Instagram", "https://www.instagram.com/margovantesmodas");
    addSocialNetwork(
        marGovantes, socialNetworks, "TikTok", "https://www.tiktok.com/@margovantesmodas");
    addSocialNetwork(marGovantes, socialNetworks, "Teléfono", "tel:+34670080639");

    Product mg_americanaMarina =
        createProduct(
            "Americana Marina",
            4599,
            null,
            "Americana marina, perfecta para looks formales o de oficina.",
            productTypes.get("Chaqueta"),
            marGovantes,
            "outfit_9_americana_marina_45.99.jpg");
    createVariant(mg_americanaMarina, productSizes.get("M"), productColors.get("Azul"), true);

    Product mg_pantalonesMari =
        createProduct(
            "Pantalones Marinos",
            3999,
            15,
            "Pantalones marinos, elegantes y apropiados para un look formal.",
            productTypes.get("Pantalón"),
            marGovantes,
            "outfit_9_pantalones_marinos_39.99.jpg");
    createVariant(mg_pantalonesMari, productSizes.get("M"), productColors.get("Azul"), true);

    Product mg_camisaFloral =
        createProduct(
            "Camisa Floral",
            2399,
            null,
            "Camisa floral, una opción fresca y alegre para el buen tiempo.",
            productTypes.get("Camiseta"),
            marGovantes,
            "outfit_10_camisa_floral_23.99.jpg");
    createVariant(mg_camisaFloral, productSizes.get("M"), productColors.get("Rosa"), true);

    Product mg_pantalonesVerdes =
        createProduct(
            "Pantalones Verdes",
            3499,
            null,
            "Pantalones verdes, ideales para un look casual de primavera.",
            productTypes.get("Pantalón"),
            marGovantes,
            "outfit_10_pantalones_verdes_34.99.jpg");
    createVariant(mg_pantalonesVerdes, productSizes.get("M"), productColors.get("Verde"), true);

    Product mg_sueterVerde =
        createProduct(
            "Suéter Verde",
            4599,
            20,
            "Suéter verde, apropiado para el entretiempo y los días frescos.",
            productTypes.get("Chaqueta"),
            marGovantes,
            "outfit_10_suéter_verde_45_99.jpg");
    createVariant(mg_sueterVerde, productSizes.get("M"), productColors.get("Verde"), true);

    Outfit mg_outfit1 =
        createOutfit("Negocio Oceánico", 0, null, marGovantes, "outfit_9_negocio_oceánico.jpg");
    createOutfitTagRelation(mg_outfit1, outfitTags.get("Formal"));
    createOutfitProduct(mg_outfit1, mg_americanaMarina, 0);
    createOutfitProduct(mg_outfit1, mg_pantalonesMari, 1);

    Outfit mg_outfit2 =
        createOutfit("Río Interior", 1, null, marGovantes, "outfit_10_río_interior.jpg");
    createOutfitTagRelation(mg_outfit2, outfitTags.get("Casual"));
    createOutfitProduct(mg_outfit2, mg_camisaFloral, 0);
    createOutfitProduct(mg_outfit2, mg_pantalonesVerdes, 1);
    createOutfitProduct(mg_outfit2, mg_sueterVerde, 2);
  }

  private void loadClientData() {
    Random rng = new Random(props.getRandomSeed());

    List<String> phoneNumbers = loadTextFile("seed/phone-numbers.txt");
    List<String> addresses = loadTextFile("seed/addresses.txt");
    List<String> firstNames = loadTextFile("seed/client-first-names.txt");
    List<String> surnames = loadTextFile("seed/client-surnames.txt");

    List<Store> allStores = storeRepository.findAll();
    List<Product> allProducts = productRepository.findAll();

    User clientUser = new User();
    clientUser.setId(seedUuid("user:client@client.com"));
    clientUser.setEmail("client@client.com");
    clientUser.setPassword(passwordEncoder.encode("Password123!"));
    userRepository.save(clientUser);

    Client manualClient = new Client();
    manualClient.setId(seedUuid("client:client@client.com"));
    manualClient.setName("Ana");
    manualClient.setSurname("García");
    manualClient.setEmail("client@client.com");
    manualClient.setPhone("+34 623456789");
    manualClient.setAddress("Calle San Fernando, nº 12, Sevilla");
    manualClient.setUser(clientUser);
    clientRepository.save(manualClient);

    Product p1 = allProducts.size() > 0 ? allProducts.get(0) : null;
    Product p3 = allProducts.size() > 2 ? allProducts.get(2) : p1;
    Product p4 = allProducts.size() > 3 ? allProducts.get(3) : p1;

    if (p3 != null && p4 != null && p1 != null) {
      Order order = new Order();
      order.setUser(clientUser);
      order.setOrderDate(LocalDateTime.now());
      order.setOrderStatus(OrderStatus.PENDING);
      order.setOrderCode("ORD-MANUAL-001");
      order.setItems(new ArrayList<>());
      addItemsToOrder(order, List.of(p3));

      Order orderConfirmed = new Order();
      orderConfirmed.setUser(clientUser);
      orderConfirmed.setOrderDate(LocalDateTime.now().minusDays(2));
      orderConfirmed.setOrderStatus(OrderStatus.CONFIRMED);
      orderConfirmed.setOrderCode("ORD-CONFIRM-002");
      orderConfirmed.setItems(new ArrayList<>());
      addItemsToOrder(orderConfirmed, List.of(p3));
      orderRepository.save(orderConfirmed);

      Order orderRejected = new Order();
      orderRejected.setUser(clientUser);
      orderRejected.setOrderDate(LocalDateTime.now().minusDays(5));
      orderRejected.setOrderStatus(OrderStatus.REJECTED);
      orderRejected.setOrderCode("ORD-REJECT-003");
      orderRejected.setItems(new ArrayList<>());
      addItemsToOrder(orderRejected, List.of(p4));
      orderRepository.save(orderRejected);

      Order orderPicked = new Order();
      orderPicked.setUser(clientUser);
      orderPicked.setOrderDate(LocalDateTime.now().minusDays(1));
      orderPicked.setOrderStatus(OrderStatus.PICKED);
      orderPicked.setOrderCode("ORD-PICKED-004");
      orderPicked.setItems(new ArrayList<>());
      addItemsToOrder(orderPicked, List.of(p1, p4));
      orderRepository.save(orderPicked);
    }

    for (int i = 1; i <= props.getClientCount(); i++) {
      String name = pick(firstNames, rng);
      String surname = pick(surnames, rng);
      String clientEmail = "client" + i + "@client.com";

      User user = new User();
      user.setId(seedUuid("user:" + clientEmail));
      user.setEmail(clientEmail);
      user.setPassword(passwordEncoder.encode("Password123!"));
      userRepository.save(user);

      Client client = new Client();
      client.setId(seedUuid("client:" + clientEmail));
      client.setName(name);
      client.setSurname(surname);
      client.setEmail(clientEmail);
      client.setPhone(pick(phoneNumbers, rng));
      client.setAddress(pick(addresses, rng));
      client.setUser(user);
      clientRepository.save(client);

      if (rng.nextDouble() < 0.5 && !allProducts.isEmpty()) {
        Order randomOrder = new Order();
        randomOrder.setUser(user);
        randomOrder.setOrderDate(LocalDateTime.now().minusDays(rng.nextInt(10)));
        randomOrder.setOrderStatus(OrderStatus.PENDING);
        randomOrder.setOrderCode(
            "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        randomOrder.setItems(new ArrayList<>());

        int itemsToCreate = 1 + rng.nextInt(3);
        int randomTotal = 0;

        for (int k = 0; k < itemsToCreate; k++) {
          Product p = pick(allProducts, rng);
          OrderItem item = new OrderItem();
          item.setOrder(randomOrder);
          item.setProduct(p);
          item.setQuantity(1 + rng.nextInt(2));
          item.setPriceAtPurchase(p.getPriceInCents());
          randomOrder.getItems().add(item);
          randomTotal += item.getPriceAtPurchase() * item.getQuantity();
        }
        randomOrder.setTotalPrice(randomTotal);
        orderRepository.save(randomOrder);
      }

      int followCount = 1 + rng.nextInt(3);
      List<Store> shuffled = new ArrayList<>(allStores);
      for (int j = 0; j < followCount && j < shuffled.size(); j++) {
        int idx = j + rng.nextInt(shuffled.size() - j);
        Store s = shuffled.get(idx);
        shuffled.set(idx, shuffled.get(j));
        shuffled.set(j, s);

        StoreFollower follower = new StoreFollower();
        follower.setClient(client);
        follower.setStore(s);
        storeFollowerRepository.save(follower);
      }
    }
  }

  private Store createStore(
      String name,
      String email,
      double lat,
      double lon,
      String address,
      String openingHours,
      String primaryColor,
      String secondaryColor,
      String bannerImageFilename) {

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
    store.setAcceptsShipping(false);
    store.setStorefront(storefront);
    store.setUser(user);
    storeRepository.save(store);

    return store;
  }

  private void addSocialNetwork(
      Store store, Map<String, SocialNetwork> socialNetworks, String type, String link) {
    StoreSocialNetwork ssn = new StoreSocialNetwork();
    ssn.setLink(link);
    ssn.setSocialNetwork(socialNetworks.get(type));
    ssn.setStore(store);
    storeSocialNetworkRepository.save(ssn);
  }

  private Product createProduct(
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

  private void createVariant(
      Product product, ProductSize size, ProductColor color, boolean isAvailable) {
    ProductVariant variant = new ProductVariant();
    variant.setProduct(product);
    variant.setSize(size);
    variant.setColor(color);
    variant.setIsAvailable(isAvailable);
    productVariantRepository.save(variant);
  }

  private Outfit createOutfit(
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

  private String uploadImage(String filename) {
    if (filename == null) return null;
    String publicId = filename.replaceAll("\\.[^.]+$", "").replace(" ", "_");
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

  private <T> T pick(List<T> list, Random rng) {
    return list.get(rng.nextInt(list.size()));
  }

  private void addItemsToOrder(Order order, List<Product> products) {
    int total = 0;
    for (Product p : products) {
      OrderItem item = new OrderItem();
      item.setOrder(order);
      item.setProduct(p);
      item.setQuantity(1);
      item.setPriceAtPurchase(p.getPriceInCents());
      order.getItems().add(item);
      total += p.getPriceInCents();
    }
    order.setTotalPrice(total);
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

  private UUID seedUuid(String name) {
    return UUID.nameUUIDFromBytes(
        ("seed:" + props.getRandomSeed() + ":" + name).getBytes(StandardCharsets.UTF_8));
  }
}
