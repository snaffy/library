package pl.ki.az.reservations.repository;

import org.springframework.stereotype.Repository;
import pl.ki.az.reservations.domainaggregates.BookReservation;
import pl.ki.az.shared.model.book.BookId;


@Repository
public interface ReservationRepository {
    BookReservation loadBooksReservation(BookId bookId);

    void save(BookReservation userRental);
}
