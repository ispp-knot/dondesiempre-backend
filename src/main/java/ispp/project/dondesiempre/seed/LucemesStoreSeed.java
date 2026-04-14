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
            "Hay complementos que visten, y luego están los que te definen. En Luceme's Bags creemos que un bolso es mucho más que un accesorio: es tu compañero fiel del día a día, el que guarda tus pequeños tesoros, tus prisas, tus llaves y esa libreta donde apuntas tus ideas.\r\n"
                + //
                "\r\n"
                + //
                "Situados en la céntrica calle San Sebastián, número 6, en pleno corazón de Dos Hermanas, somos una tienda especializada en bolsos, carteras y complementos de piel y materiales cuidadosamente seleccionados. No buscamos seguir todas las tendencias pasajeras, sino ofrecerte diseños atemporales, con calidad real y un precio justo.\r\n"
                + //
                "\r\n"
                + //
                "¿Qué nos hace diferentes?\r\n"
                + //
                "\r\n"
                + //
                "👜 Variedad sin agobios: Desde bolsos de diario amplios y resistentes hasta modelos más elegantes para tus noches o eventos especiales.\r\n"
                + //
                "\r\n"
                + //
                "✨ Piel y materiales con carácter: Toca, siente y comprueba tú misma la diferencia de una buena hebilla, un buen forro y unos acabados que duran.\r\n"
                + //
                "\r\n"
                + //
                "💬 Trato de toda la vida: Aquí no eres un código de barras. Te conocemos, te aconsejamos y si no encuentras lo que buscas, lo buscamos juntas.\r\n"
                + //
                "\r\n"
                + //
                "📍 Tienda física en Dos Hermanas: Aunque también hacemos envíos, sabemos que un bolso se prueba, se mira bien y se huele. Por eso nuestra puerta está siempre abierta en la calle San Sebastián, 6.\r\n"
                + //
                "\r\n"
                + //
                "En Luceme's Bags no vendemos bolsos por inercia. Los elegimos uno a uno pensando en mujeres reales, con ritmos reales y estilos propios. Porque el bolso perfecto no es el más caro, sino el que te hace sentir tú misma cada vez que lo coges al salir de casa.\r\n"
                + //
                "\r\n"
                + //
                "Pásate a conocernos.\r\n"
                + //
                "Te esperamos con una sonrisa, buenos precios y esa pieza que aún no sabes que necesitas… pero que cuando la veas, no podrás dejar escapar.\r\n"
                + //
                "\r\n"
                + //
                "Luceme's Bags – Tu bolso, tu estilo, tu tienda en Dos Hermanas.",
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
