package carsharingapp.repository;

import carsharingapp.model.Rental;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RentalRepository extends JpaRepository<Rental, Long> {
    List<Rental> getRentalsByUser_Id(Long userId);

    Optional<Rental> findByUser_IdAndId(Long userId, Long rentalId);

    List<Rental> findByReturnDateAfterOrActualReturnDateIsNotNull(LocalDate now);

    List<Rental> findByReturnDateBeforeAndActualReturnDateIsNull(LocalDate now);
}
