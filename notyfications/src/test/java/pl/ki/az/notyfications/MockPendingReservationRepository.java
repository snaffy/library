package pl.ki.az.notyfications;

import pl.ki.az.notyfications.repository.PendingReservationRepository;
import pl.ki.az.shared.model.book.BookId;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MockPendingReservationRepository implements PendingReservationRepository {
    private List<PendingReservation> pendingReservationList = new ArrayList<>();

    @Override
    public PendingReservation loadPendingReservations(BookId bookId) {
        return findPendingReservationByBookId(bookId);
    }

    @Override
    public void save(PendingReservation pendingReservation) {
        pendingReservationList.add(pendingReservation);
    }

    private PendingReservation findPendingReservationByBookId(BookId bookId) {
        final Optional<PendingReservation> foundPendingReservation = pendingReservationList.stream().filter(pendingReservation -> pendingReservation.getBookId().equals(bookId))
                .findFirst();
        return foundPendingReservation.orElse(null);
    }
}
