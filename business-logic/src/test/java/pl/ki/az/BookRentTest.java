package pl.ki.az;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pl.ki.az.api.RentalSerivce;
import pl.ki.az.api.RentalServiceAPI;
import pl.ki.az.model.book.Book;
import pl.ki.az.model.book.BookId;
import pl.ki.az.model.client.Client;
import pl.ki.az.model.client.UserId;
import pl.ki.az.rent.RentResult;
import pl.ki.az.rent.UserRental;
import pl.ki.az.rent.UserRentalFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

class BookRentTest {
    private MockClientRepository clientRepository;
    private MockBookRepository bookRepository;
    private UserRentalFactory userRentalFactory;
    private RentalSerivce rentalSerivce;
    private MockRentalRepository mockRentalRepository;
    private Map<UserId, BookId> rentOrders = new HashMap<>();
    private Map<UserId, RentResult> rentResults = new HashMap<>();

    @BeforeEach
    private void setUp() {
        this.clientRepository = new MockClientRepository();
        this.bookRepository = new MockBookRepository();
        this.userRentalFactory = new UserRentalFactory();
        this.mockRentalRepository = new MockRentalRepository();
        this.rentalSerivce = new RentalServiceAPI(mockRentalRepository,bookRepository,userRentalFactory);
    }

    @Test
    @DisplayName("rent an available book")
    void testCase1() {
        //given
        inSystemExistBooks().withId(1L).withId(2L).create();
        client().withId(1L).wouldLikeToRentABookWithId(1L).create();

        //when
        rentBook();

        //then
        rentResult().clientWithId(1L).successfullyRentedBookWithId(1L);
    }

    @Test
    @DisplayName("rent a book that does not exist")
    void testCase2() {
        //given
        inSystemExistBooks().withId(2L).create();
        client().withId(1L).wouldLikeToRentABookWithId(1L).create();

        //when
        rentBook();

        //then
        rentResult().clientWithId(1L)
                .notSuccessfullyRentedBook()
                .becauseBookNotExistInSystem();
    }

    @Test
    @DisplayName("two clients rent two different books")
    void testCase3() {
        //given
        inSystemExistBooks().withId(1L).withId(2L).create();
        client().withId(1L).wouldLikeToRentABookWithId(1L).create();
        client().withId(2L).wouldLikeToRentABookWithId(2L).create();
        //when
        rentBook();

        //then
        rentResult().clientWithId(1L)
                .successfullyRentedBookWithId(1L);
        rentResult().clientWithId(2L)
                .successfullyRentedBookWithId(2L);
    }

    @Test
    @DisplayName("two clients rent two different books and books have been rented to right people")
    void testCase4() {
        //given
        inSystemExistBooks().withId(1L).withId(2L).create();
        client().withId(1L).wouldLikeToRentABookWithId(1L).create();
        client().withId(2L).wouldLikeToRentABookWithId(2L).create();
        //when
        rentBook();

        //then
        rentResult().clientWithId(1L)
                .successfullyRentedBookWithId(1L)
                .notRentedBookWithId(2L);
        rentResult().clientWithId(2L)
                .successfullyRentedBookWithId(2L)
                .notRentedBookWithId(1L);
    }

//    @Test
//    @DisplayName("two clients rent the same book")
//    void testCase4() {
//        //given
//        inSystemExistBooks().withId(1L).create();
//        client().withId(1L).wouldLikeToRentABookWithId(1L).create();
//        client().withId(2L).wouldLikeToRentABookWithId(1L).create();
//        //when
//        rentBook();
//
//        //then
//        rentResult().clientWithId(1L)
//                .successfullyRentedBookWithId().withId();
//        rentResult().clientWithId(2L)
//                .successfullyRentedBookWithId().withId();
//    }


    private RentResultAssembler rentResult(){
        return new RentResultAssembler();
    }

    private void rentBook() {
        rentOrders.forEach((userId, bookId) -> {
            final RentResult rentResult = rentalSerivce.rentBook(userId, bookId);
            rentResults.put(userId,rentResult);
        });
    }

    private ExistingBookAssembler inSystemExistBooks(){
        return new ExistingBookAssembler();
    }

    private BookRentAssembler client() {
        return new BookRentAssembler();
    }

    private class ExistingBookAssembler{

        private List<Book> books = new ArrayList<>();

        private ExistingBookAssembler withId(Long bookId) {
            books.add(new Book(new BookId(bookId)));
            return this;
        }
        void create(){
            bookRepository.addBooks(books);
        }

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
            final Client client = new Client(userId);
            final UserRental userRental = userRentalFactory.create(client);

            mockRentalRepository.save(userRental);
            rentOrders.put(userId, bookId);
        }

    }

    private class RentResultAssembler{
        private UserId client;


        private RentResultAssembler clientWithId(Long userId) {
            this.client =  new UserId(userId);
            return this;
        }

        RentResultAssembler notRentedBookWithId(long bookId) {
            UserRental userRental = mockRentalRepository.loadUsersRent(client);
            boolean isUserRentedSpecificBook = userRental.isUserRentedSpecificBook(new BookId(bookId));
            assertThat(isUserRentedSpecificBook, is(false));
            return this;
        }

        RentResultAssembler successfullyRentedBookWithId(long bookId){
            RentResult rentResult = rentResults.get(client);
            assertThat(rentResult.isValid(), is(true));

            Book rentedBook = bookRepository.findBookById(new BookId(bookId));
            assertThat(rentedBook.isAvailable(), is(false));
            return this;
        }

        RentResultAssembler notSuccessfullyRentedBook(){
            RentResult rentResult = rentResults.get(client);
            assertThat(rentResult.isValid(), is(false));
            return this;
        }

        RentResultAssembler becauseBookNotExistInSystem(){
            RentResult rentResult = rentResults.get(client);
            assertThat(rentResult.getResult(), is(RentResult.Result.BOOK_NOT_EXIST));
            return this;
        }

        RentResultAssembler becauseBookIsNotAvailable(){
            RentResult rentResult = rentResults.get(client);
            assertThat(rentResult.getResult(), is(RentResult.Result.BOOK_NOT_AVAILABLE));
            return this;
        }
    }

}
