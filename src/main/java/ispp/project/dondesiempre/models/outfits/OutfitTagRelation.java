package ispp.project.dondesiempre.models.outfits;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import ispp.project.dondesiempre.modules.common.models.BaseEntity;

@Entity
@Getter
@Setter
@Table(name = "outfit_tag_relation")
public class OutfitTagRelation extends BaseEntity {
  @NotNull
  @ManyToOne(optional = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  private Outfit outfit;

  @NotNull
  @ManyToOne(optional = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  private OutfitTag tag;
}
