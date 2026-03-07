package ispp.project.dondesiempre.modules.products.models;

import ispp.project.dondesiempre.modules.common.models.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "product_colors")
public class ProductColor extends BaseEntity {

  @Column
  @NotNull
  @Size(max = 255)
  String color;
}
