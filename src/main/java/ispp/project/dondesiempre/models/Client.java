package ispp.project.dondesiempre.models;

import ispp.project.dondesiempre.validators.Phone;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.locationtech.jts.geom.Point;

@Entity
@Getter
@Setter
@Table(name = "clients")
public class Client extends BaseEntity {

  @Column
  @NotBlank
  @Size(max = 255)
  String name;

  @Column
  @NotBlank
  @Size(max = 255)
  String surname;

  @Column @NotBlank @Email String email;

  @Column @Phone String phone;

  @Column(columnDefinition = "geometry(Point, 4326)")
  @JdbcTypeCode(SqlTypes.GEOMETRY)
  Point address;
}
