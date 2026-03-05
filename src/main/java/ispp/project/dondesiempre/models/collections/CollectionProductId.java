package ispp.project.dondesiempre.models.collections;

import java.io.Serializable;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class CollectionProductId implements Serializable {
  private UUID collection;
  private UUID product;
}
