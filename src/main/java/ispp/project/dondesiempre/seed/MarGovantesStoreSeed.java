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

class MarGovantesStoreSeed {

  private final DataSeeder s;

  MarGovantesStoreSeed(DataSeeder seeder) {
    this.s = seeder;
  }

  void seed(
      Map<String, SocialNetwork> socialNetworks,
      Map<String, ProductType> productTypes,
      Map<String, ProductColor> productColors,
      Map<String, ProductSize> productSizes,
      Map<String, OutfitTag> outfitTags) {
    // ── 9. Mar Govantes ───────────────────────────────────────────────────────
    Store marGovantes =
        s.createStore(
            "Mar Govantes",
            "demo@margovantes.com",
            37.282795534740714,
            -5.924663169087747,
            "41701 Dos Hermanas, Sevilla",
            "Lun,Mar,Jue,Vie: 9:30-14:00. Mie: 9:30-14:00, 5:30-8:30. Sab 10:00-13:30",
            "#000045",
            "#844652",
            "govantes/escaparate_mar_govantes.jpg",
            "👗Tienda de ropa especializada en #moda mujer en Dos Hermanas (Sevilla).\n"
                + //
                "Ropa casual y de vestir.\n"
                + //
                "🏷️Tallas grandes.");
    s.addSocialNetwork(
        marGovantes, socialNetworks, "Facebook", "https://www.facebook.com/margovantesmodas/");
    s.addSocialNetwork(
        marGovantes, socialNetworks, "Instagram", "https://www.instagram.com/margovantesmodas");
    s.addSocialNetwork(
        marGovantes, socialNetworks, "TikTok", "https://www.tiktok.com/@margovantesmodas");
    s.addSocialNetwork(marGovantes, socialNetworks, "Teléfono", "tel:+34670080639");

    Product mg_americanaMarina =
        s.createProduct(
            "Americana Marina",
            4599,
            null,
            "Americana marina, perfecta para looks formales o de oficina.",
            productTypes.get("Chaqueta"),
            marGovantes,
            "govantes/outfit_9_americana_marina_45.99.jpg");
    s.createVariant(mg_americanaMarina, productSizes.get("M"), productColors.get("Azul"), true);

    Product mg_pantalonesMari =
        s.createProduct(
            "Pantalones Marinos",
            3999,
            15,
            "Pantalones marinos, elegantes y apropiados para un look formal.",
            productTypes.get("Pantalón"),
            marGovantes,
            "govantes/outfit_9_pantalones_marinos_39.99.jpg");
    s.createVariant(mg_pantalonesMari, productSizes.get("M"), productColors.get("Azul"), true);

    Product mg_camisaFloral =
        s.createProduct(
            "Camisa Floral",
            2399,
            null,
            "Camisa floral, una opción fresca y alegre para el buen tiempo.",
            productTypes.get("Camiseta"),
            marGovantes,
            "govantes/outfit_10_camisa_floral_23.99.jpg");
    s.createVariant(mg_camisaFloral, productSizes.get("M"), productColors.get("Rosa"), true);

    Product mg_pantalonesVerdes =
        s.createProduct(
            "Pantalones Verdes",
            3499,
            null,
            "Pantalones verdes, ideales para un look casual de primavera.",
            productTypes.get("Pantalón"),
            marGovantes,
            "govantes/outfit_10_pantalones_verdes_34.99.jpg");
    s.createVariant(mg_pantalonesVerdes, productSizes.get("M"), productColors.get("Verde"), true);

    Product mg_sueterVerde =
        s.createProduct(
            "Suéter Verde",
            4599,
            20,
            "Suéter verde, apropiado para el entretiempo y los días frescos.",
            productTypes.get("Chaqueta"),
            marGovantes,
            "govantes/outfit_10_suéter_verde_45_99.jpg");
    s.createVariant(mg_sueterVerde, productSizes.get("M"), productColors.get("Verde"), true);

    Outfit mg_outfit1 =
        s.createOutfit(
            "Negocio Oceánico", 0, null, marGovantes, "govantes/outfit_9_negocio_oceánico.jpg");
    s.createOutfitTagRelation(mg_outfit1, outfitTags.get("Formal"));
    s.createOutfitProduct(mg_outfit1, mg_americanaMarina, 0);
    s.createOutfitProduct(mg_outfit1, mg_pantalonesMari, 1);

    Outfit mg_outfit2 =
        s.createOutfit("Río Interior", 1, null, marGovantes, "govantes/outfit_10_río_interior.jpg");
    s.createOutfitTagRelation(mg_outfit2, outfitTags.get("Casual"));
    s.createOutfitProduct(mg_outfit2, mg_camisaFloral, 0);
    s.createOutfitProduct(mg_outfit2, mg_pantalonesVerdes, 1);
    s.createOutfitProduct(mg_outfit2, mg_sueterVerde, 2);
  }
}
