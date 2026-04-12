package ispp.project.dondesiempre.modules.products.dtos;

import ispp.project.dondesiempre.modules.products.models.ProductColor;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductColorDTO {

  private UUID id;
  private String name;
  private String hexCode;

  public ProductColorDTO(ProductColor color) {
    this.id = color.getId();
    this.name = color.getColor();
    this.hexCode = parseHexCode(color.getColor());
  }

  private String parseHexCode(String color) {
    if (color == null || color.isEmpty()) {
      return "#cccccc";
    }
    if (color.startsWith("#")) {
      return color;
    }
    return getDefaultColorForName(color.toLowerCase());
  }

  private String getDefaultColorForName(String colorName) {
    return switch (colorName) {
      case "negro", "black" -> "#1a1a1a";
      case "blanco", "white" -> "#f5f5f5";
      case "rojo", "red" -> "#c0392b";
      case "azul", "blue" -> "#2980b9";
      case "verde", "green" -> "#27ae60";
      case "amarillo", "yellow" -> "#f1c40f";
      case "marino", "navy" -> "#1e3a5f";
      case "gris", "gray", "grey" -> "#7f8c8d";
      case "rosa", "pink" -> "#e91e63";
      case "naranja", "orange" -> "#e67e22";
      case "morado", "purple" -> "#9b59b6";
      case "marron", "marrón", "brown" -> "#795548";
      case "beige" -> "#d7ccc8";
      default -> "#cccccc";
    };
  }
}
