package ispp.project.dondesiempre.modules.orders.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import ispp.project.dondesiempre.modules.auth.models.User;
import ispp.project.dondesiempre.modules.common.models.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "orders")
public class Order extends BaseEntity{

    @Column
    @NotBlank
    @Size(max = 255)
    String orderCode;

    @Column
    @NotNull
    LocalDateTime orderDate;

    @Column
    @Enumerated(EnumType.STRING)
    OrderStatus orderStatus;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();
}
