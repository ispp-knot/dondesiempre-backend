package ispp.project.dondesiempre.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Getter
@Table(name = "examples")
public class Example extends BaseEntity {

    @Column
    String name;

}
