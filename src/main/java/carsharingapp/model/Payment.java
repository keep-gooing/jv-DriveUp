package carsharingapp.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@Setter
@SQLDelete(sql = "UPDATE payments SET is_deleted = true WHERE id =?")
@SQLRestriction("is_deleted = false")
@Table(name = "payments")
public class Payment {
    @Id
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id", nullable = false)
    private Rental rental;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Type type;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    private boolean isDeleted = false;

    private String sessionUrl;

    private String sessionId;

    private BigDecimal amountToPay;

    public enum Type {
        PAYMENT,
        FINE
    }

    public enum Status {
        PENDING,
        PAID
    }
}
