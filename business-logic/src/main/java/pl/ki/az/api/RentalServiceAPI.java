package pl.ki.az.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.ki.az.domainaggregates.RentResult;
import pl.ki.az.domainaggregates.ReturnResult;
import pl.ki.az.domainaggregates.UserRental;
import pl.ki.az.domainaggregates.UserRentalFactory;
import pl.ki.az.exceptions.BookNotExist;
import pl.ki.az.exceptions.UsersRentNotFound;
import pl.ki.az.model.book.Book;
import pl.ki.az.model.book.BookId;
import pl.ki.az.model.client.Client;
import pl.ki.az.model.client.UserId;
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

        if(usersRent == null)
            throw new UsersRentNotFound();

        Book book = bookRepository.findBookById(bookId);

        if (book == null) {
            return RentResult.bookNotExist();
        }

        final RentResult rentResult = usersRent.rentBook(book);
        rentalRepository.save(usersRent);

        return rentResult;
    }

    @Override
    public ReturnResult returnBook(BookId bookId) {
        UserRental usersRent = rentalRepository.loadUsersRentByRentedBook(bookId);

        if(usersRent == null)
            throw new UsersRentNotFound();

        Book book = bookRepository.findBookById(bookId);

        if(book == null)
            throw new BookNotExist();

        return usersRent.returnBook(book);
    }


}
