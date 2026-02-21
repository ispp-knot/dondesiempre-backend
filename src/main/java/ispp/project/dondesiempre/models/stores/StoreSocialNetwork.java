package ispp.project.dondesiempre.models.stores;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import ispp.project.dondesiempre.models.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "storeSocialNetworks")
public class StoreSocialNetwork extends BaseEntity {

    @Column(length = 500)
    @NotNull
    @Size(max = 500)
    String link;

    @NotNull
    @ManyToOne(optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private SocialNetwork socialNetwork;

    @NotNull
    @ManyToOne(optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Store store;

}
