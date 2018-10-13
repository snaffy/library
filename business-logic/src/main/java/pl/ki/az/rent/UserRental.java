package pl.ki.az.rent;

import pl.ki.az.model.book.Book;
import pl.ki.az.model.client.Client;

import java.util.ArrayList;
import java.util.List;

public class UserRental {
    private Client client;
    private List<Book> borrowedBooks;

    public UserRental(Client client) {
        this.client = client;
        this.borrowedBooks = new ArrayList<>();
    }

    public void returBook() {
    }

    public void rentBook(Book book) {
        borrowedBooks.add(book);
    }
}
