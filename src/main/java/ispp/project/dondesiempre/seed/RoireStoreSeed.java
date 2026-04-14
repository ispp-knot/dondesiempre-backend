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

class RoireStoreSeed {

  private final DataSeeder s;

  RoireStoreSeed(DataSeeder seeder) {
    this.s = seeder;
  }

  void seed(
      Map<String, SocialNetwork> socialNetworks,
      Map<String, ProductType> productTypes,
      Map<String, ProductColor> productColors,
      Map<String, ProductSize> productSizes,
      Map<String, OutfitTag> outfitTags) {
    // ── 4. Roire ──────────────────────────────────────────────────────────────
    Store roire =
        s.createStore(
            "Roire",
            "demo@roire.com",
            37.280633989355685,
            -5.920593858571272,
            "C. San Sebastián, 15, 41701 Dos Hermanas, Sevilla",
            "Lun-Vie 10:00-13:45, 17:30-21:00, Sábado 10:00-14:00",
            "#ad0000",
            "#633a00",
            "roire/escaparate_roire.png",
            "En Roire no concebimos la moda como un producto más. Para nosotros, cada prenda, cada tejido y cada cliente merecen la misma exigencia: la de la calidad real y el compromiso honesto.\r\n"
                + "\r\n"
                + "Por eso nos aseguramos de que solo vendemos productos que cumplen con los estándares más altos de nuestros clientes. Pero no nos quedamos ahí. Escuchamos, observamos y aprendemos de quienes confían en nosotros día tras día. Nos esforzamos por comprender las necesidades de nuestros clientes leales y trabajamos para ofrecerles un servicio personalizado, adaptado a sus gustos, su talla, su momento de vida y sus exigencias.\r\n"
                + "\r\n"
                + "En Roire no vendemos por vender. Vendemos para que vuelvas.\r\n"
                + "\r\n"
                + "Porque la fidelidad no se pide, se construye. Y nosotros la construimos prenda a prenda, conversación a conversación, con calidad, cercanía y una sola obsesión: que te sientas única, bien atendida y con ganas de repetir.",
            "acct_1TFzN97Xt9jc8bVg");
    s.addSocialNetwork(roire, socialNetworks, "WhatsApp", "https://wa.me/34641231378");
    s.addSocialNetwork(roire, socialNetworks, "Instagram", "https://www.instagram.com/roire.es");
    s.addSocialNetwork(roire, socialNetworks, "Web", "https://tiendaroire.es/");
    s.addSocialNetwork(roire, socialNetworks, "Teléfono", "tel:+34641231378");

    Product roire_faldaCebra =
        s.createProduct(
            "Falda Cebra",
            2999,
            20,
            "Falda cebra con estilo, ideal para un look casual llamativo.",
            productTypes.get("Pantalón"),
            roire,
            "roire/outfit6_falda_cebra_29.99.jpg");
    s.createVariant(roire_faldaCebra, productSizes.get("M"), productColors.get("Blanco"), true);

    Product roire_sueterMarron =
        s.createProduct(
            "Suéter Marrón",
            4999,
            null,
            "Suéter marrón, muy abrigado para los días de frío.",
            productTypes.get("Chaqueta"),
            roire,
            "roire/outfit6_suéter_marrón_49.99.jpg");
    s.createVariant(roire_sueterMarron, productSizes.get("M"), productColors.get("Beige"), true);

    Outfit roire_outfit1 =
        s.createOutfit("Savana Otoñal", 0, 20, roire, "roire/outfit6_savana_otoñal.jpg");
    s.createOutfitTagRelation(roire_outfit1, outfitTags.get("Casual"));
    s.createOutfitProduct(roire_outfit1, roire_faldaCebra, 0);
    s.createOutfitProduct(roire_outfit1, roire_sueterMarron, 1);
  }
}
