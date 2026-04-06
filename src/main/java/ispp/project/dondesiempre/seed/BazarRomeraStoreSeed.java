package ispp.project.dondesiempre.seed;

import ispp.project.dondesiempre.modules.outfits.models.OutfitTag;
import ispp.project.dondesiempre.modules.products.models.ProductColor;
import ispp.project.dondesiempre.modules.products.models.ProductSize;
import ispp.project.dondesiempre.modules.products.models.ProductType;
import ispp.project.dondesiempre.modules.stores.models.SocialNetwork;
import ispp.project.dondesiempre.modules.stores.models.Store;
import java.util.Map;

class BazarRomeraStoreSeed {

  private final DataSeeder s;

  BazarRomeraStoreSeed(DataSeeder seeder) {
    this.s = seeder;
  }

  void seed(
      Map<String, SocialNetwork> socialNetworks,
      Map<String, ProductType> productTypes,
      Map<String, ProductColor> productColors,
      Map<String, ProductSize> productSizes,
      Map<String, OutfitTag> outfitTags) {
    // ── 8. Bazar Romera ───────────────────────────────────────────────────────
    Store bazarRomera =
        s.createStore(
            "Bazar Romera",
            "demo@bazarromera.com",
            37.28156704784245,
            -5.921647213639721,
            "C. Romera, 8, 41701 Dos Hermanas, Sevilla",
            "Lun-Vie: 9:00-14:00, 17:00-21:00. Sab 9:00-14:00",
            "#255d56",
            "#247054",
            "romera/escaparate_bazar_romera.png",
            "Romera, 8. 41701 Dos Hermanas (Sevilla)",
            "acct_1TFzN97Xt9jc8bVg");
    s.addSocialNetwork(bazarRomera, socialNetworks, "Teléfono", "tel:+34631122308");

    s.createProduct(
        "Camisa Azul",
        3499,
        null,
        "Camisa azul, un básico cómodo para el día a día.",
        productTypes.get("Camiseta"),
        bazarRomera,
        "romera/producto_camisa_azul_34.99.jpg");
    s.createProduct(
        "Chandal Negro",
        2999,
        20,
        "Chándal negro, cómodo y apropiado para el día a día.",
        productTypes.get("Pantalón"),
        bazarRomera,
        "romera/producto_chandal_negro_29.99.jpg");
  }
}
