package ispp.project.dondesiempre.models.products;

import ispp.project.dondesiempre.models.BaseEntity;
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
@Table(name = "colors")
public class Color extends BaseEntity {

    @Column
    @NotNull
    @Size(max = 255)
    String color;

}