package pl.ki.az.repository;

import pl.ki.az.model.book.Book;
import pl.ki.az.model.book.BookId;

public interface BookRepository {
    Book findBookById(BookId bookId);
}
