package pl.ki.az.notyfications;


import pl.ki.az.shared.model.book.BookId;
import pl.ki.az.shared.model.client.ClientId;

import java.util.TreeSet;

public class PendingReservation {
    private BookId bookId;
    private TreeSet<PendingClient> pendingClients;

    PendingReservation(BookId bookId) {
        this.bookId = bookId;
        this.pendingClients = new TreeSet<>();
    }

    void addClientToPendingQueue(ClientId clientId) {
        PendingClient pendingClient = new PendingClient(clientId);
        pendingClients.add(pendingClient);
    }

    void notifyAboutBookAvailability(NotificationService notificationService) {
        if (!pendingClients.isEmpty()) {
            PendingClient firstClientInQueueForBook = pendingClients.first();
            notificationService.notifyClientAboutBookAvailability(firstClientInQueueForBook.getClientId());
            pendingClients.remove(firstClientInQueueForBook);
        }
    }

    int countPendingClients() {
        return pendingClients.size();
    }

    BookId getBookId() {
        return bookId;
    }
}
