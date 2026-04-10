package ispp.project.dondesiempre.modules.stores.models;

import ispp.project.dondesiempre.modules.auth.models.User;
import ispp.project.dondesiempre.modules.common.models.BaseEntity;
import ispp.project.dondesiempre.modules.common.validators.Phone;
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
import java.util.Optional;
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

  @Column String accountId;
  @Column @NotNull Boolean premiumPlan = false;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "storefront_id", referencedColumnName = "id", nullable = false)
  private Storefront storefront;

  @NotNull
  @OneToOne(optional = false)
  @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
  private User user;

  public Optional<String> getPhone() {
    return Optional.ofNullable(this.phone);
  }

  public Optional<String> getAboutUs() {
    return Optional.ofNullable(this.aboutUs);
  }

  public Optional<String> getAccountId() {
    return Optional.ofNullable(this.accountId);
  }
}
