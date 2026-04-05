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

class PineappleStoreSeed {

  private final DataSeeder s;

  PineappleStoreSeed(DataSeeder seeder) {
    this.s = seeder;
  }

  void seed(
      Map<String, SocialNetwork> socialNetworks,
      Map<String, ProductType> productTypes,
      Map<String, ProductColor> productColors,
      Map<String, ProductSize> productSizes,
      Map<String, OutfitTag> outfitTags) {
    // ── 5. Pineapple Moda ─────────────────────────────────────────────────────
    Store pineapple =
        s.createStore(
            "Pineapple Moda",
            "demo@pineapplemoda.com",
            37.28212759058594,
            -5.921767187403529,
            "C. Canónigo, 73, 41701 Dos Hermanas, Sevilla",
            "Lun-Vie 10:30-13:30, 17:30-20:30, Sab 10:30-13:30",
            "#75502b",
            "#333333",
            "pineapple/escaparate_pineapple.png",
            "Calle Canónigo 73 Dos Hermanas | 41071 | Sevilla");
    s.addSocialNetwork(pineapple, socialNetworks, "WhatsApp", "https://wa.me/34644807498");
    s.addSocialNetwork(
        pineapple,
        socialNetworks,
        "Facebook",
        "https://www.facebook.com/p/Pineapple-Moda-100070328611143/");
    s.addSocialNetwork(
        pineapple, socialNetworks, "Instagram", "https://www.instagram.com/pineapple.sevilla/");
    s.addSocialNetwork(
        pineapple, socialNetworks, "TikTok", "https://www.tiktok.com/@pineapplesevilla");
    s.addSocialNetwork(pineapple, socialNetworks, "Web", "https://pineapplemoda.com/");
    s.addSocialNetwork(pineapple, socialNetworks, "Teléfono", "+34644807498");

    Product pine_camisaBlanca =
        s.createProduct(
            "Camisa Blanca",
            1599,
            null,
            "Camisa blanca, un básico de armario para cualquier época del año.",
            productTypes.get("Camiseta"),
            pineapple,
            "pineapple/outfit7_camisa_blanca_15.99.jpg");
    s.createVariant(pine_camisaBlanca, productSizes.get("M"), productColors.get("Blanco"), true);

    Product pine_faldaRoja =
        s.createProduct(
            "Falda Roja",
            4599,
            10,
            "Falda roja, perfecta para darle color al otoño.",
            productTypes.get("Pantalón"),
            pineapple,
            "pineapple/outfit7_falda_roja_45.99.jpg");
    s.createVariant(pine_faldaRoja, productSizes.get("M"), productColors.get("Rojo"), true);

    Outfit pine_outfit1 =
        s.createOutfit(
            "Pasión de Otoño", 0, 30, pineapple, "pineapple/outfit7_pasión_de_otoño.jpg");
    s.createOutfitTagRelation(pine_outfit1, outfitTags.get("Casual"));
    s.createOutfitProduct(pine_outfit1, pine_camisaBlanca, 0);
    s.createOutfitProduct(pine_outfit1, pine_faldaRoja, 1);
  }
}
