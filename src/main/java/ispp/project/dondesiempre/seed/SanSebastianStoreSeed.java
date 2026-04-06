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

class SanSebastianStoreSeed {

  private final DataSeeder s;

  SanSebastianStoreSeed(DataSeeder seeder) {
    this.s = seeder;
  }

  void seed(
      Map<String, SocialNetwork> socialNetworks,
      Map<String, ProductType> productTypes,
      Map<String, ProductColor> productColors,
      Map<String, ProductSize> productSizes,
      Map<String, OutfitTag> outfitTags) {
    // ── 3. Confecciones y Hogar San Sebastián ─────────────────────────────────
    Store sanSebastian =
        s.createStore(
            "Confecciones y Hogar San Sebastián",
            "demo@confeccionesyhogarsansebastian.com",
            37.27961006469284,
            -5.920175962489425,
            "C/ San Sebastián, 35, 41701 Dos Hermanas, Sevilla",
            "Lun-Vie 9:45-21:00, Sab 9:30-13:30",
            "#000000",
            "#ab327d",
            "sebastian/escaparate_san_sebastian.png",
            "Somos una empresa local física, llevamos en el mercado desde 1959, trabajamos cada día para satisfacer las necesidades de nuestros clientes.\n"
                + "\n"
                + "Nos dedicamos a la venta de confección de caballero y señora, tallas especiales, interiores de caballeros, señoras y niñ@s, todo tipo de paquetería y calcetería y también trabajamos el textil de hogar.\n"
                + "\n"
                + "Nuestros proveedores son todos nacionales, como son Harper and Neyer, La Bassette, Kents, Dos Galgos, Brown Fury, Carlos Córdoba, Capelhi, Persam, Tachi and Zucca, Boguar, Egatex, Señoretta, Ysabel Mora, Dolores Cortés, Selmark, Belty, Abanderado, Ferry, Avet, Set, Morante, Naiara, Selene, Burrito Blanco, Catotex, Cañete, Manterol, Belnou, Cotopour, etc…\n"
                + "\n"
                + "Si busca un artículo y no lo encuentra, intentamos conseguirlo poniendo todo lo que está en nuestras manos para satisfacer al cliente.",
            "acct_1TFzN97Xt9jc8bVg");
    s.addSocialNetwork(
        sanSebastian,
        socialNetworks,
        "Instagram",
        "https://www.instagram.com/tejidossansebastian/");
    s.addSocialNetwork(sanSebastian, socialNetworks, "WhatsApp", "https://wa.me/34691537089");
    s.addSocialNetwork(
        sanSebastian, socialNetworks, "Web", "https://www.confeccionesyhogarsansebastian.com/");
    s.addSocialNetwork(sanSebastian, socialNetworks, "Teléfono", "tel:+34691537089");

    Product ss_albornoz =
        s.createProduct(
            "Albornoz Blanco",
            1999,
            null,
            "Albornoz blanco, perfecto para después del baño o la playa.",
            productTypes.get("Accesorio"),
            sanSebastian,
            "sebastian/outfit5_albornoz_blanco_19.99.jpg");
    s.createVariant(ss_albornoz, productSizes.get("M"), productColors.get("Blanco"), true);

    Product ss_crocs =
        s.createProduct(
            "Crocs Negras",
            1599,
            15,
            "Crocs negras, cómodas para el uso diario en casa o en la calle.",
            productTypes.get("Zapatos"),
            sanSebastian,
            "sebastian/outfit5_crocs_negras_15.99.jpg");
    s.createVariant(ss_crocs, productSizes.get("M"), productColors.get("Negro"), true);

    Outfit ss_outfit1 =
        s.createOutfit(
            "Tranquilidad Casera",
            0,
            null,
            sanSebastian,
            "sebastian/outfit5_tranquilidad_casera.jpg");
    s.createOutfitTagRelation(ss_outfit1, outfitTags.get("Casual"));
    s.createOutfitProduct(ss_outfit1, ss_albornoz, 0);
    s.createOutfitProduct(ss_outfit1, ss_crocs, 1);
  }
}
