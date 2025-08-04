package carsharingapp.notification;

import carsharingapp.exception.NotificationException;
import carsharingapp.model.Payment;
import carsharingapp.model.Rental;

public interface NotificationService {
    void sendNotification(Long userId, String message) throws NotificationException;

    void sendRentalCreationNotification(Rental rental) throws NotificationException;

    void sendRentalReturnNotification(Rental rental) throws NotificationException;

    void sendOverdueNotification(Rental rental) throws NotificationException;

    void sendNoOverdueRentalsNotification(Rental rental) throws NotificationException;

    void sendPaymentSuccessNotification(Payment payment) throws NotificationException;

    void sendPaymentCancelNotification(Payment payment) throws NotificationException;
}
