package ispp.project.dondesiempre.models.stores;

import ispp.project.dondesiempre.models.BaseEntity;
import ispp.project.dondesiempre.models.storefronts.Storefront;
import ispp.project.dondesiempre.validators.Phone;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.locationtech.jts.geom.Point;

@Entity
@Getter
@Setter
@Table(name = "stores")
public class Store extends BaseEntity {

  @Column
  @NotBlank
  @Size(max = 255)
  String name;

  @Column @NotBlank @Email String email;

  // NIF
  @Column
  @NotBlank
  @Size(max = 255)
  String storeID;

  @Column(columnDefinition = "geometry(Point, 4326)")
  @JdbcTypeCode(SqlTypes.GEOMETRY)
  @NotNull
  Point location;

  @Column
  @Size(max = 255)
  @NotBlank
  String address;

  @Column
  @NotBlank
  @Size(max = 255)
  String openingHours;

  @Column @Phone String phone;

  @Column(columnDefinition = "TEXT")
  @Size(max = 5000)
  String aboutUs;

  @Column @NotNull Boolean acceptsShipping;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "storefront_id", referencedColumnName = "id", nullable = false)
  private Storefront storefront;
}
