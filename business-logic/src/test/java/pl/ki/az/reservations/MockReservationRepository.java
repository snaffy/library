package pl.ki.az.reservations;


import pl.ki.az.reservations.domainaggregates.BookReservation;
import pl.ki.az.reservations.repository.ReservationRepository;
import pl.ki.az.shared.model.book.BookId;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MockReservationRepository implements ReservationRepository {
    private final List<BookReservation> allBooksBookReservation = new ArrayList<>();

    @Override
    public BookReservation loadBooksReservation(BookId bookId) {
        Optional<BookReservation> result = allBooksBookReservation.stream()
                .filter(reservation -> reservation.getBookId().equals(bookId))
                .findFirst();

        return result.orElse(null);
    }

    @Override
    public void save(BookReservation bookReservation) {
        allBooksBookReservation.add(bookReservation);
    }

    void saveAllReservations(List<BookReservation> bookReservations) {
        allBooksBookReservation.addAll(bookReservations);
    }

}
