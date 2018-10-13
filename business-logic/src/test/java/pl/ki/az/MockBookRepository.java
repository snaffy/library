package pl.ki.az;

import pl.ki.az.model.book.Book;
import pl.ki.az.model.book.BookId;
import pl.ki.az.repository.BookRepository;

import java.util.ArrayList;
import java.util.List;

public class MockBookRepository implements BookRepository {
    private List<Book> existingBooks = new ArrayList<>();

    @Override
    public Book findBookById(BookId bookId) {
        return null;
    }

    public void addBook(Book book) {
        existingBooks.add(book);
    }
}
