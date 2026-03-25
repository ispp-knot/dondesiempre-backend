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

class GretaClosetStoreSeed {

  private final DataSeeder s;

  GretaClosetStoreSeed(DataSeeder seeder) {
    this.s = seeder;
  }

  void seed(
      Map<String, SocialNetwork> socialNetworks,
      Map<String, ProductType> productTypes,
      Map<String, ProductColor> productColors,
      Map<String, ProductSize> productSizes,
      Map<String, OutfitTag> outfitTags) {
    // ── 1. Greta Closet ──────────────────────────────────────────────────────
    Store greta =
        s.createStore(
            "Greta Closet",
            "demo@gretacloset.com",
            37.283160057885304,
            -5.9237761491318235,
            "C. Sta. María Magdalena, 14, 41701 Dos Hermanas, Sevilla",
            "Lun-Vie 10:00-13:00, 17:30-20:30. Sab 10:30-14:00",
            "#000000",
            "#a1005c",
            "greta/escaparate_greta_closet.png",
            "C. Sta. María Magdalena, 14, 41701 Dos Hermanas, Sevilla, España");
    s.addSocialNetwork(
        greta, socialNetworks, "Instagram", "https://www.instagram.com/gretacloset/");
    s.addSocialNetwork(
        greta, socialNetworks, "TikTok", "https://www.tiktok.com/@gretaclosetcomplementos");
    s.addSocialNetwork(greta, socialNetworks, "WhatsApp", "https://wa.me/34694466991");
    s.addSocialNetwork(greta, socialNetworks, "Web", "https://gretacloset.com/");
    s.addSocialNetwork(greta, socialNetworks, "Teléfono", "tel:+34694466991");

    Product greta_vestidoBlanco =
        s.createProduct(
            "Vestido Blanco",
            4999,
            null,
            "Vestido blanco, perfecto para los días de calor.",
            productTypes.get("Vestido"),
            greta,
            "greta/outfit1_vestido_blanco_49.99.jpg");
    s.createVariant(greta_vestidoBlanco, productSizes.get("M"), productColors.get("Blanco"), true);

    Product greta_botasNegras1 =
        s.createProduct(
            "Botas Negras",
            6999,
            15,
            "Botas negras, ideales para los meses de frío.",
            productTypes.get("Zapatos"),
            greta,
            "greta/outfit1_botas_negras_69.99.jpg");
    s.createVariant(greta_botasNegras1, productSizes.get("M"), productColors.get("Negro"), true);

    Product greta_bolsoMarron =
        s.createProduct(
            "Bolso Marrón",
            3499,
            null,
            "Bolso marrón de estilo moderno, perfecto para el día a día.",
            productTypes.get("Accesorio"),
            greta,
            "greta/outfit1_bolso_marron_34.99.jpg");
    s.createVariant(greta_bolsoMarron, productSizes.get("M"), productColors.get("Beige"), true);

    Product greta_vestidoAzul =
        s.createProduct(
            "Vestido Azul",
            6999,
            20,
            "Vestido azul, perfecto para una tarde de verano.",
            productTypes.get("Vestido"),
            greta,
            "greta/outfit2_vestido_azul_69.99.jpg");
    s.createVariant(greta_vestidoAzul, productSizes.get("M"), productColors.get("Azul"), true);

    Product greta_bolsoBeige =
        s.createProduct(
            "Bolso Beige",
            4999,
            null,
            "Bolso beige de estilo moderno, ideal para el día a día.",
            productTypes.get("Accesorio"),
            greta,
            "greta/outfit2_bolso_beige_49.99.jpg");
    s.createVariant(greta_bolsoBeige, productSizes.get("M"), productColors.get("Beige"), true);

    Product greta_pendientes =
        s.createProduct(
            "Pendientes Mariposa Oro",
            1999,
            10,
            "Pendientes mariposa dorados, un toque elegante para cualquier look.",
            productTypes.get("Accesorio"),
            greta,
            "greta/outfit2_pendientes_mariposa_oro_19.99.jpg");
    s.createVariant(greta_pendientes, productSizes.get("S"), productColors.get("Beige"), true);

    Product greta_chaqueta_lunares =
        s.createProduct(
            "Chaqueta Lunares",
            1999,
            10,
            "Chaqueta corta negra con lunares blancos.",
            productTypes.get("Chaqueta"),
            greta,
            "greta/chaqueta_lunares_lola.png");
    s.createVariant(
        greta_chaqueta_lunares, productSizes.get("S"), productColors.get("Negro"), true);

    Product greta_falda_lunares =
        s.createProduct(
            "Falda Lunares",
            1999,
            10,
            "Falda larga  negra con lunares blancos.",
            productTypes.get("Chaqueta"),
            greta,
            "greta/falda_lunares_lola.png");
    s.createVariant(greta_falda_lunares, productSizes.get("S"), productColors.get("Negro"), true);

    Outfit greta_outfit1 =
        s.createOutfit("Conjunto Mihai", 0, null, greta, "greta/outfit1_conjunto_mihai.jpg");
    s.createOutfitTagRelation(greta_outfit1, outfitTags.get("Elegante"));
    s.createOutfitProduct(greta_outfit1, greta_vestidoBlanco, 0);
    s.createOutfitProduct(greta_outfit1, greta_botasNegras1, 1);
    s.createOutfitProduct(greta_outfit1, greta_bolsoMarron, 2);

    Outfit greta_outfit2 =
        s.createOutfit("Conjunto Galilea", 1, 25, greta, "greta/outfit2_conjunto_galilea.jpg");
    s.createOutfitTagRelation(greta_outfit2, outfitTags.get("Casual"));
    s.createOutfitProduct(greta_outfit2, greta_vestidoAzul, 0);
    s.createOutfitProduct(greta_outfit2, greta_bolsoBeige, 1);
    s.createOutfitProduct(greta_outfit2, greta_pendientes, 2);

    Outfit greta_outfit_lola =
        s.createOutfit("Conjunto Lola", 2, 25, greta, "greta/conjunto_lola.png");
    s.createOutfitTagRelation(greta_outfit_lola, outfitTags.get("Casual"));
    s.createOutfitProduct(greta_outfit_lola, greta_chaqueta_lunares, 0);
    s.createOutfitProduct(greta_outfit_lola, greta_falda_lunares, 1);
  }
}
