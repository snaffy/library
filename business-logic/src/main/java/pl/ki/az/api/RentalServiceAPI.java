package pl.ki.az.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.ki.az.model.book.Book;
import pl.ki.az.model.book.BookId;
import pl.ki.az.model.client.Client;
import pl.ki.az.model.client.UserId;
import pl.ki.az.rent.RentResult;
import pl.ki.az.rent.UserRental;
import pl.ki.az.rent.UserRentalFactory;
import pl.ki.az.repository.BookRepository;
import pl.ki.az.repository.RentalRepository;

@Service
public class RentalServiceAPI implements RentalSerivce {

    private final RentalRepository rentalRepository;
    private final BookRepository bookRepository;
    private final UserRentalFactory userRentalFactory;

    @Autowired
    public RentalServiceAPI(RentalRepository rentalRepository, BookRepository bookRepository, UserRentalFactory userRentalFactory) {
        this.rentalRepository = rentalRepository;
        this.bookRepository = bookRepository;
        this.userRentalFactory = userRentalFactory;
    }

    public void createUsersRent(Client client) {
        UserRental userRental = userRentalFactory.create(client);
        rentalRepository.save(userRental);
    }

    @Override
    public RentResult rentBook(UserId userID, BookId bookId) {
        UserRental usersRent = rentalRepository.loadUsersRent(userID);

        Book book = bookRepository.findBookById(bookId);

        if (book == null) {
            return RentResult.bookNotExist();
        }

        if (!book.isAvailabe()) {
            return RentResult.bookIsNotAvailable();
        }

        usersRent.rentBook(book);
        rentalRepository.save(usersRent);

        return RentResult.successfullyRentedBook();
    }


}
