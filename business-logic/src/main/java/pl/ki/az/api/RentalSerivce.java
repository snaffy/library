package pl.ki.az.api;


import pl.ki.az.domainaggregates.RentResult;
import pl.ki.az.domainaggregates.ReturnResult;
import pl.ki.az.model.book.BookId;
import pl.ki.az.model.client.UserId;

public interface RentalSerivce {
    RentResult rentBook(UserId userId, BookId bookId);

    ReturnResult returnBook(BookId bookId);
}
