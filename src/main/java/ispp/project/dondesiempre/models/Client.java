package ispp.project.dondesiempre.models;

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
@Table(name = "clients")
public class Client extends BaseEntity {

    @Column
    @NotNull
    @Size(max = 255)
    String name;

    @Column
    @NotNull
    @Size(max = 255)
    String surname;

}
