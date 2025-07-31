package mate.academy.carsharingapp.notification;

import mate.academy.carsharingapp.exception.NotificationException;
import mate.academy.carsharingapp.model.Payment;
import mate.academy.carsharingapp.model.Rental;

public interface NotificationService {
    void sendNotification(Long userId, String message) throws NotificationException;

    void sendRentalCreationNotification(Rental rental) throws NotificationException;

    void sendRentalReturnNotification(Rental rental) throws NotificationException;

    void sendOverdueNotification(Rental rental) throws NotificationException;

    void sendNoOverdueRentalsNotification(Rental rental) throws NotificationException;

    void sendPaymentSuccessNotification(Payment payment) throws NotificationException;

    void sendPaymentCancelNotification(Payment payment) throws NotificationException;
}
