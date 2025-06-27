package mate.academy.carsharingapp.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@SQLDelete(sql = "UPDATE cars SET is_deleted = true WHERE id =?")
@SQLRestriction("is_deleted = false")
@Table(name = "cars")
public class Car {
    //fmemfpsk
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String model;
    @Column(nullable = false)
    private String brand;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Type type;
    private int inventory;
    @Column(name = "daily_fee", precision = 10, scale = 2)
    private BigDecimal dailyFee;
    private boolean isDeleted = false;
    public enum Type {
        SEDAN,
        SUV,
        HATCHBACK,
        UNIVERSAL
    }
}
