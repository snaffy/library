package pl.ki.az.notyfications.repository;

import pl.ki.az.notyfications.PendingReservation;
import pl.ki.az.shared.model.book.BookId;

public interface PendingReservationRepository {
    PendingReservation loadPendingReservations(BookId bookId);
    void save(PendingReservation pendingReservation);
}
