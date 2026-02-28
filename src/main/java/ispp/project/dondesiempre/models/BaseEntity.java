package ispp.project.dondesiempre.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import java.util.UUID;
import lombok.EqualsAndHashCode;

@MappedSuperclass
@EqualsAndHashCode(of = {"id"})
public class BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  protected UUID id;

  @Version private Integer version;

  public UUID getId() {
    return id;
  }

  public Integer getVersion() {
    return version;
  }

  public void setVersion(Integer version) {
    this.version = version;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  @JsonIgnore
  public boolean isNew() {
    return this.id == null;
  }
}
