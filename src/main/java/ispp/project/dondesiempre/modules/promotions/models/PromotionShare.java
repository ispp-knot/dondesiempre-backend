package ispp.project.dondesiempre.modules.promotions.models;

import ispp.project.dondesiempre.modules.common.models.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(
        name = "promotion_shares")
public class PromotionShare extends BaseEntity {

    @NotNull
    @ManyToOne(optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Promotion promotion;

    @Column
    @NotNull
    private LocalDate date;

}
