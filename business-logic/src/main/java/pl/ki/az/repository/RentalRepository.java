package pl.ki.az.repository;

import org.springframework.stereotype.Repository;
import pl.ki.az.domainaggregates.UserRental;
import pl.ki.az.model.book.BookId;
import pl.ki.az.model.client.UserId;

@Repository
public interface RentalRepository {
    UserRental loadUsersRent(UserId userId);

    UserRental loadUsersRentByRentedBook(BookId bookId);

    void save(UserRental userRental);
}
