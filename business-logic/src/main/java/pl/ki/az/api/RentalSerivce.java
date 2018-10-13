package pl.ki.az.api;


import pl.ki.az.model.book.BookId;
import pl.ki.az.model.client.UserId;
import pl.ki.az.rent.RentResult;

public interface RentalSerivce {
    RentResult rentBook(UserId userId, BookId bookId);
}
