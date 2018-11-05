package pl.ki.az.reservations.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.ki.az.reservations.domainaggregates.BookReservation;
import pl.ki.az.reservations.domainaggregates.RentResult;
import pl.ki.az.reservations.domainaggregates.ReturnResult;
import pl.ki.az.reservations.exceptions.BookReservationNotFound;
import pl.ki.az.reservations.repository.ReservationRepository;
import pl.ki.az.shared.BookingPendingNotification;
import pl.ki.az.shared.model.book.BookId;
import pl.ki.az.shared.model.client.ClientId;

@Service
public class ReservationServiceAPI implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final BookingPendingNotification bookingPendingNotification;

    @Autowired
    public ReservationServiceAPI(ReservationRepository reservationRepository, BookingPendingNotification bookingPendingNotification) {
        this.reservationRepository = reservationRepository;
        this.bookingPendingNotification = bookingPendingNotification;
    }

    @Override
    public RentResult rentBook(ClientId clientId, BookId bookId) {
        BookReservation bookReservation = reservationRepository.loadBooksReservation(bookId);

        if (bookReservation == null)
            throw new BookReservationNotFound();

        final RentResult rentResult = bookReservation.rentBookForClient(clientId);

        if (rentResult.isBookNotAvailable()){
            bookingPendingNotification.subscribeToBookAvailability(clientId, bookId);
            return rentResult;
        }

        reservationRepository.save(bookReservation);
        return rentResult;
    }

    @Override
    public ReturnResult returnBook(BookId bookId) {
        BookReservation bookReservation = reservationRepository.loadBooksReservation(bookId);

        if (bookReservation == null)
            throw new BookReservationNotFound();

        final ReturnResult returnResult = bookReservation.returnBook();

        if(returnResult.isValid()){
            bookingPendingNotification.notifyAboutBookAvailability(bookId);
        }

        return returnResult;
    }


}
