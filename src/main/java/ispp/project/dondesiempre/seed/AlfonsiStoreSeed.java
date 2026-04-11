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

class AlfonsiStoreSeed {

  private final DataSeeder s;

  AlfonsiStoreSeed(DataSeeder seeder) {
    this.s = seeder;
  }

  void seed(
      Map<String, SocialNetwork> socialNetworks,
      Map<String, ProductType> productTypes,
      Map<String, ProductColor> productColors,
      Map<String, ProductSize> productSizes,
      Map<String, OutfitTag> outfitTags) {
    // ── 7. Confecciones Alfonsi ───────────────────────────────────────────────
    Store alfonsi =
        s.createStore(
            "Confecciones Alfonsi",
            "demo@alfonsi.com",
            37.28073691177687,
            -5.922528642533732,
            "Calle Romera, 32, 41701 Dos Hermanas, Sevilla",
            "Horarios sin confirmar",
            "#183b27",
            "#214a1b",
            "alfonsi/escaparate_alfonsi.png",
            "En la calle Romera, número 32, seguimos haciendo lo que mejor sabemos: vestir con oficio, cariño y confianza. Confecciones Alfonsi es una de esas tiendas de barrio que resisten con orgullo, donde cada cliente sabe que encontrará calidad, variedad y una sonrisa sincera.\r\n"
                + //
                "\r\n"
                + //
                "Nos dedicamos a la confección y venta de prendas para toda la familia, con especial atención a los detalles, los tejidos duraderos y las tallas que de verdad se adaptan a ti. Ni moda desechable ni tendencias imposibles: aquí priman el sentido común y el buen gusto.\r\n"
                + //
                "\r\n"
                + //
                "Si buscas una tienda donde te conozcan, te aconsejen y te cuiden… ya sabes dónde estamos. Confecciones Alfonsi: la confianza se cose, como la ropa de antes.",
            "acct_1TFzN97Xt9jc8bVg");
    s.addSocialNetwork(
        alfonsi, socialNetworks, "Facebook", "https://www.facebook.com/confecciones.alfonsi/");

    Product alf_parkaBlanca =
        s.createProduct(
            "Parka Blanca",
            7999,
            30,
            "Parka blanca, ideal para afrontar el invierno con estilo.",
            productTypes.get("Chaqueta"),
            alfonsi,
            "alfonsi/outfit8_parka_blanca_79.99.jpg");
    s.createVariant(alf_parkaBlanca, productSizes.get("M"), productColors.get("Blanco"), true);

    Product alf_pantalonesNegros =
        s.createProduct(
            "Pantalones Negros",
            4699,
            null,
            "Pantalones negros, un básico imprescindible para cualquier temporada.",
            productTypes.get("Pantalón"),
            alfonsi,
            "alfonsi/outfit8_pantalones_negros_46.99.jpg");
    s.createVariant(alf_pantalonesNegros, productSizes.get("M"), productColors.get("Negro"), true);

    Product alf_botasNegras =
        s.createProduct(
            "Botas Negras",
            8999,
            null,
            "Botas negras, perfectas para los días fríos de invierno.",
            productTypes.get("Zapatos"),
            alfonsi,
            "alfonsi/outfit8_botas_negras_89.99.jpg");
    s.createVariant(alf_botasNegras, productSizes.get("M"), productColors.get("Negro"), true);

    Outfit alf_outfit1 =
        s.createOutfit("Cómodo Invierno", 0, null, alfonsi, "alfonsi/outfit8_cómodo_invierno.jpg");
    s.createOutfitTagRelation(alf_outfit1, outfitTags.get("Invierno"));
    s.createOutfitProduct(alf_outfit1, alf_parkaBlanca, 0);
    s.createOutfitProduct(alf_outfit1, alf_pantalonesNegros, 1);
    s.createOutfitProduct(alf_outfit1, alf_botasNegras, 2);
  }
}
