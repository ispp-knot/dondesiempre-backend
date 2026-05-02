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
            "Llevamos en el mercado desde 1959. Más de seis décadas vistiendo a familias enteras de Dos Hermanas y haciendo del comercio local un verdadero hogar. Somos una empresa física, de toda la vida, con la puerta siempre abierta y el trato que ya casi no se encuentra.\r\n"
                + //
                "\r\n"
                + //
                "¿Qué ofrecemos?\r\n"
                + //
                "Un poco de todo, pero hecho con mucho mimo:\r\n"
                + //
                "\r\n"
                + //
                "👔 Confección de caballero y señora.\r\n"
                + //
                "\r\n"
                + //
                "📏 Tallas especiales (porque los cuerpos reales merecen ropa real).\r\n"
                + //
                "\r\n"
                + //
                "🧦 Interiores de caballeros, señoras y niños.\r\n"
                + //
                "\r\n"
                + //
                "📦 Paquetería y calcetería de calidad.\r\n"
                + //
                "\r\n"
                + //
                "🛋️ Textil de hogar: desde sábanas hasta mantelería.\r\n"
                + //
                "\r\n"
                + //
                "¿De dónde viene nuestra ropa?\r\n"
                + //
                "Solo trabajamos con proveedores 100% nacionales: Harper & Neyer, La Bassette, Kents, Dos Galgos, Brown Fury, Carlos Córdoba, Capelhi, Persam, Tachi & Zucca, Boguar, Egatex, Señoretta, Ysabel Mora, Dolores Cortés, Selmark, Belty, Abanderado, Ferry, Avet, Set, Morante, Naiara, Selene, Burrito Blanco, Catotex, Cañete, Manterol, Belnou, Cotopour… y muchos más.\r\n"
                + //
                "\r\n"
                + //
                "Y si no lo tenemos… lo buscamos.\r\n"
                + //
                "Porque nuestra mayor satisfacción es resolver lo que necesitas, aunque no esté en el escaparate. Si buscas un artículo y no lo encuentras, intentamos conseguirlo poniendo todo lo que está en nuestras manos para satisfacerte.\r\n"
                + //
                "\r\n"
                + //
                "Confecciones y Hogar San Sebastián: tradición, calidad y trato de familia desde 1959.",
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
    s.createVariant(ss_albornoz, productSizes.get("S"), productColors.get("Blanco"), true);
    s.createVariant(ss_albornoz, productSizes.get("L"), productColors.get("Blanco"), true);

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
    s.createVariant(ss_crocs, productSizes.get("S"), productColors.get("Negro"), true);
    s.createVariant(ss_crocs, productSizes.get("L"), productColors.get("Negro"), true);

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
