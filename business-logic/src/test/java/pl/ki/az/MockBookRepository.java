package pl.ki.az;

import pl.ki.az.model.book.Book;
import pl.ki.az.model.book.BookId;
import pl.ki.az.repository.BookRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MockBookRepository implements BookRepository {
    private List<Book> existingBooks = new ArrayList<>();

    @Override
    public Book findBookById(BookId bookId) {
        Optional<Book> result = existingBooks.stream()
                .filter(book -> book.getId().equals(bookId))
                .findFirst();

        return result.orElse(null);
    }

    void addBooks(List<Book> books) {
        existingBooks.addAll(books);
    }
}
