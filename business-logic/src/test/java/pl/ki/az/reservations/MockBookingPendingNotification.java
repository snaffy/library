package pl.ki.az.reservations;

import pl.ki.az.shared.BookingPendingNotification;
import pl.ki.az.shared.model.book.BookId;
import pl.ki.az.shared.model.client.ClientId;

public class MockBookingPendingNotification implements BookingPendingNotification {

    @Override
    public void subscribeToBookAvailability(ClientId clientId, BookId bookId) {

    }

    @Override
    public void notifyAboutBookAvailability(BookId clientId) {

    }
}
