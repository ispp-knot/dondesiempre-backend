package ispp.project.dondesiempre.modules.common.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Version;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import org.springframework.data.domain.Persistable;

@MappedSuperclass
@EqualsAndHashCode(of = {"id"})
public class BaseEntity implements Persistable<UUID> {

  @Id protected UUID id;

  @Version private Integer version;

  @PrePersist
  private void assignId() {
    if (id == null) id = UUID.randomUUID();
  }

  @Override
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

  // Uses version instead of id so that entities with a pre-assigned id (e.g. seeded
  // entities) are still treated as new and trigger an INSERT rather than a UPDATE.
  @Override
  @JsonIgnore
  public boolean isNew() {
    return this.version == null;
  }
}
