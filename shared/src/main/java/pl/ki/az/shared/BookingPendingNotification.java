package pl.ki.az.shared;

import pl.ki.az.shared.model.book.BookId;
import pl.ki.az.shared.model.client.ClientId;

public interface BookingPendingNotification {
    void subscribeToBookAvailability(ClientId clientId, BookId bookId);
    void notifyAboutBookAvailability(BookId clientId);

}
