package pl.ki.az.domainaggregates;

import pl.ki.az.model.book.Book;
import pl.ki.az.model.book.BookId;
import pl.ki.az.model.client.Client;

import java.util.HashSet;
import java.util.Set;

public class UserRental {
    private Client client;
    private Set<Book> borrowedBooks;

    UserRental(Client client) {
        this.client = client;
        this.borrowedBooks = new HashSet<>();
    }

    public ReturnResult returnBook(Book book) {
        borrowedBooks.stream()
                .filter(bookToRemove -> bookToRemove.equals(book))
                .findFirst()
                .ifPresent(Book::changeStatusToAvailable);
        borrowedBooks.removeIf(bookToRemove -> bookToRemove.equals(book));

        return ReturnResult.successfullyReturnedBook();
    }

    public RentResult rentBook(Book book) {
        if (!book.isAvailable()) {
            return RentResult.bookIsNotAvailable();
        }

        book.changeStatusToRented();
        borrowedBooks.add(book);

        return RentResult.successfullyRentedBook();
    }

    public boolean isUserRentedSpecificBook(BookId bookId){
        return borrowedBooks.stream()
                .anyMatch(searchedBookId -> searchedBookId.getId().equals(bookId));
    }

    public Client getClient() {
        return client;
    }
}
