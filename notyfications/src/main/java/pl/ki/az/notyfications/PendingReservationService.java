package pl.ki.az.notyfications;

import org.springframework.beans.factory.annotation.Autowired;
import pl.ki.az.notyfications.repository.ClientRepository;
import pl.ki.az.notyfications.repository.PendingReservationRepository;
import pl.ki.az.shared.BookingPendingNotification;
import pl.ki.az.shared.model.book.BookId;
import pl.ki.az.shared.model.client.ClientId;

public class PendingReservationService implements BookingPendingNotification {

    private PendingReservationRepository pendingReservationRepository;
    private NotificationService notificationService;

    @Autowired
    PendingReservationService(PendingReservationRepository pendingReservationRepository, ClientRepository clientRepository) {
        this.pendingReservationRepository = pendingReservationRepository;
        this.notificationService = new NotificationService(clientRepository);
    }

    @Override
    public void subscribeToBookAvailability(ClientId clientId, BookId bookId) {
        PendingReservation pendingReservation = pendingReservationRepository.loadPendingRevervations(bookId);

        if (pendingReservation == null) {
            pendingReservation = new PendingReservation(bookId);
        }

        pendingReservation.addClientToPendingQueue(clientId);

        pendingReservationRepository.save(pendingReservation);
    }

    @Override
    public void notifyAboutBookAvailability(BookId bookId) {
        PendingReservation pendingReservation = pendingReservationRepository.loadPendingRevervations(bookId);

        if (pendingReservation == null) {
            throw new PendingReservationNotFound();
        }

        pendingReservation.notifyAboutBookAvailability(notificationService);
    }
}
