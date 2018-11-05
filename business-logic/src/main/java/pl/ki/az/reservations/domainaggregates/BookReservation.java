package pl.ki.az.reservations.domainaggregates;
import pl.ki.az.shared.model.book.BookId;
import pl.ki.az.shared.model.client.ClientId;

public class BookReservation {
    private ClientId clientId;
    private final BookId bookId;

    public BookReservation(BookId bookId) {
        this.clientId = null;
        this.bookId = bookId;
    }

    public ReturnResult returnBook() {
        if(isBookAvailable()){
            return ReturnResult.canNotReturnABookThatHasNotBeenBorrowedBefore();
        }

        this.clientId = null;
        return ReturnResult.successfullyReturnedBook();
    }

    public RentResult rentBookForClient(ClientId clientId) {
        if (!isBookAvailable()) {
            return RentResult.bookIsNotAvailable();
        }

        this.rentForClient(clientId);
        return RentResult.successfullyRentedBook();
    }

    private boolean isBookAvailable() {
        return clientId == null;
    }

    private void rentForClient(ClientId clientId) {
        this.clientId = clientId;
    }

    public boolean isBookBorrowedByClient(ClientId clientId) {
        return this.clientId == clientId;
    }

    public BookId getBookId() {
        return bookId;
    }
}
