package ispp.project.dondesiempre.models.products;

import ispp.project.dondesiempre.models.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "sizes")
public class Size extends BaseEntity {

    @Column
    @NotNull
    @jakarta.validation.constraints.Size(max = 255)
    String size;

}