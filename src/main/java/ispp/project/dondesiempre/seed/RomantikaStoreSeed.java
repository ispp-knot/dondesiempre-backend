package ispp.project.dondesiempre.seed;

import ispp.project.dondesiempre.modules.outfits.models.Outfit;
import ispp.project.dondesiempre.modules.outfits.models.OutfitTag;
import ispp.project.dondesiempre.modules.products.models.Product;
import ispp.project.dondesiempre.modules.products.models.ProductColor;
import ispp.project.dondesiempre.modules.products.models.ProductSize;
import ispp.project.dondesiempre.modules.products.models.ProductType;
import ispp.project.dondesiempre.modules.stores.models.SocialNetwork;
import ispp.project.dondesiempre.modules.stores.models.Store;
import java.util.Map;

class RomantikaStoreSeed {

  private final DataSeeder s;

  RomantikaStoreSeed(DataSeeder seeder) {
    this.s = seeder;
  }

  void seed(
      Map<String, SocialNetwork> socialNetworks,
      Map<String, ProductType> productTypes,
      Map<String, ProductColor> productColors,
      Map<String, ProductSize> productSizes,
      Map<String, OutfitTag> outfitTags) {
    // ── 2. Modas Romantika Vintage ────────────────────────────────────────────
    Store romantika =
        s.createStore(
            "Modas Romantika Vintage",
            "demo@romantikavintage.com",
            37.280502359992376,
            -5.920509768052806,
            "C. San Sebastián, 17, 41701 Dos Hermanas, Sevilla",
            "Lun-Vie 10:30-13:30, 17:30-20:30. Sab 10:30-13:30",
            "#315750",
            "#2b4f39",
            "romantika/escaparate_romantika.png",
            "Encuentra tus complementos originales\n"
                + "Moda para eventos y ocasiones especiales\n"
                + "Toda la moda urbana divertida y diferente que hace única a cada mujer",
            "acct_1TFzN97Xt9jc8bVg");
    s.addSocialNetwork(romantika, socialNetworks, "WhatsApp", "https://wa.me/34645142782");
    s.addSocialNetwork(
        romantika, socialNetworks, "Instagram", "https://www.instagram.com/modasromantikavintage/");
    s.addSocialNetwork(
        romantika, socialNetworks, "Facebook", "https://www.facebook.com/RomantikaVintage/");
    s.addSocialNetwork(romantika, socialNetworks, "Web", "http://www.romantikavintage.es/");
    s.addSocialNetwork(romantika, socialNetworks, "Teléfono", "tel:+34955668820");

    Product rom_vestidoRojo =
        s.createProduct(
            "Vestido Rojo",
            6499,
            null,
            "Vestido rojo, ideal para una ocasión especial en verano.",
            productTypes.get("Vestido"),
            romantika,
            "romantika/outfit3_vestido_rojo_64.99.jpg");
    s.createVariant(rom_vestidoRojo, productSizes.get("M"), productColors.get("Rojo"), true);

    Product rom_sandaliasRojas =
        s.createProduct(
            "Sandalias Rojas con Tacón",
            4699,
            25,
            "Sandalias rojas con tacón, perfectas para el buen tiempo.",
            productTypes.get("Zapatos"),
            romantika,
            "romantika/outfit3_sandalias_rojas_con_tacón_46.99.jpg");
    s.createVariant(rom_sandaliasRojas, productSizes.get("M"), productColors.get("Rojo"), true);

    Product rom_vestidoVerde =
        s.createProduct(
            "Vestido Verde",
            4999,
            null,
            "Vestido verde, una opción fresca para la primavera.",
            productTypes.get("Vestido"),
            romantika,
            "romantika/outfit4_vestido_verde_49.99.jpg");
    s.createVariant(rom_vestidoVerde, productSizes.get("M"), productColors.get("Verde"), true);

    Product rom_taconesBeige =
        s.createProduct(
            "Tacones Beige",
            4499,
            10,
            "Tacones beige, versátiles para cualquier ocasión.",
            productTypes.get("Zapatos"),
            romantika,
            "romantika/outfit4_tacones_beige_44.99.jpg");
    s.createVariant(rom_taconesBeige, productSizes.get("M"), productColors.get("Beige"), true);

    Outfit rom_outfit1 =
        s.createOutfit("Verano Rojo", 0, 10, romantika, "romantika/outfit3_verano_rojo.jpg");
    s.createOutfitTagRelation(rom_outfit1, outfitTags.get("Verano"));
    s.createOutfitProduct(rom_outfit1, rom_vestidoRojo, 0);
    s.createOutfitProduct(rom_outfit1, rom_sandaliasRojas, 1);

    Outfit rom_outfit2 =
        s.createOutfit(
            "Primavera Verde", 1, 15, romantika, "romantika/outfit4_primavera_verde.jpg");
    s.createOutfitTagRelation(rom_outfit2, outfitTags.get("Verano"));
    s.createOutfitProduct(rom_outfit2, rom_vestidoVerde, 0);
    s.createOutfitProduct(rom_outfit2, rom_taconesBeige, 1);
  }
}
