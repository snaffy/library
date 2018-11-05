package pl.ki.az.reservations.api;


import pl.ki.az.reservations.domainaggregates.RentResult;
import pl.ki.az.reservations.domainaggregates.ReturnResult;
import pl.ki.az.shared.model.book.BookId;
import pl.ki.az.shared.model.client.ClientId;

public interface ReservationService {
    RentResult rentBook(ClientId clientId, BookId bookId);

    ReturnResult returnBook(BookId bookId);
}
