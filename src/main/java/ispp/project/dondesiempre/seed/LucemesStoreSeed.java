package ispp.project.dondesiempre.seed;

import ispp.project.dondesiempre.modules.outfits.models.OutfitTag;
import ispp.project.dondesiempre.modules.products.models.ProductColor;
import ispp.project.dondesiempre.modules.products.models.ProductSize;
import ispp.project.dondesiempre.modules.products.models.ProductType;
import ispp.project.dondesiempre.modules.stores.models.SocialNetwork;
import ispp.project.dondesiempre.modules.stores.models.Store;
import java.util.Map;

class LucemesStoreSeed {

  private final DataSeeder s;

  LucemesStoreSeed(DataSeeder seeder) {
    this.s = seeder;
  }

  void seed(
      Map<String, SocialNetwork> socialNetworks,
      Map<String, ProductType> productTypes,
      Map<String, ProductColor> productColors,
      Map<String, ProductSize> productSizes,
      Map<String, OutfitTag> outfitTags) {
    // ── 6. Luceme's Bags ──────────────────────────────────────────────────────
    Store lucemes =
        s.createStore(
            "Luceme's Bags",
            "demo@lucemesbags.com",
            37.28090292017098,
            -5.9208650271174506,
            "C/ San Sebastián, 6 - Dos Hermanas",
            "Horarios sin confirmar",
            "#a88743",
            "#a84843",
            "luceme/escaparate_lucemes_bags.png",
            "Los mejores bolsos.",
            "acct_1TFzN97Xt9jc8bVg");
    s.addSocialNetwork(
        lucemes,
        socialNetworks,
        "Facebook",
        "https://www.facebook.com/profile.php?id=100090603545882");
    s.createProduct(
        "Bolso Crema",
        1599,
        null,
        "Bolso crema de estilo moderno, ideal para cualquier ocasión.",
        productTypes.get("Accesorio"),
        lucemes,
        "luceme/producto_bolso_crema_15.99.jpg");
    s.createProduct(
        "Bolso Crema Oro",
        1899,
        null,
        "Bolso crema con detalles dorados, un complemento elegante y atemporal.",
        productTypes.get("Accesorio"),
        lucemes,
        "luceme/producto_bolso_crema_oro_18.99.jpg");
  }
}
