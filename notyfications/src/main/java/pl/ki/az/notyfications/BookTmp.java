package pl.ki.az.notyfications;

import pl.ki.az.notyfications.model.Client;
import pl.ki.az.shared.BookingPendingNotification;
import pl.ki.az.shared.model.client.ClientId;

public class BookTmp implements BookingPendingNotification {

    private BookingPendingClients bookingPendingClients;

    public BookTmp(BookingPendingClients bookingPendingClients) {
        this.bookingPendingClients = bookingPendingClients;
    }

    @Override
    public void subscribeToBookAvailability(ClientId clientId) {
        //TODO potem w sumie można załadować z repo
        Client client = new Client(clientId);

        bookingPendingClients.register(client);
    }
}
