package pl.ki.az;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.ki.az.api.RentalSerivce;
import pl.ki.az.api.RentalServiceAPI;
import pl.ki.az.model.book.Book;
import pl.ki.az.model.book.BookId;
import pl.ki.az.model.client.Client;
import pl.ki.az.model.client.UserId;
import pl.ki.az.rent.UserRental;
import pl.ki.az.rent.UserRentalFactory;

import java.util.HashMap;
import java.util.Map;


public class BookRentTest {
    private MockClientRepository clientRepository;
    private MockBookRepository bookRepository;
    private UserRentalFactory userRentalFactory;
    private RentalSerivce rentalSerivce;
    private MockRentalRepository mockRentalRepository;
    private Map<UserId, BookId> rentOrdes = new HashMap<>();

    @BeforeEach
    private void setUp() {
        this.clientRepository = new MockClientRepository();
        this.bookRepository = new MockBookRepository();
        this.userRentalFactory = new UserRentalFactory();
        this.mockRentalRepository = new MockRentalRepository();
        this.rentalSerivce = new RentalServiceAPI(mockRentalRepository,bookRepository,userRentalFactory);
    }

    @Test
    public void dumpTest() {
        //given
        client().withId(1L).wouldLikeToRentABookWithId(1L).create();

        //when
        rentBook();

        //then


    }
    private class BookRentAssembler {
        private UserId userId;
        private BookId bookId;

        private BookRentAssembler withId(Long userId) {
            this.userId = new UserId(userId);
            return this;
        }

        private BookRentAssembler wouldLikeToRentABookWithId(Long bookId) {
            this.bookId = new BookId(bookId);
            return this;
        }

        private void create() {
            final Client client = new Client(userId, "Name_not_important", "Surname_not_important");
            final Book book = new Book(bookId);
            final UserRental userRental = userRentalFactory.create(client);
            mockRentalRepository.save(userRental);
            bookRepository.addBook(book);

            rentOrdes.put(userId, bookId);
        }

    }

    private void rentBook() {
        rentOrdes.forEach((userId, bookId) -> rentalSerivce.rentBook(userId,bookId));
    }

    private BookRentAssembler client() {
        return new BookRentAssembler();
    }
}
