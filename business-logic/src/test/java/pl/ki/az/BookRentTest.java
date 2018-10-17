package pl.ki.az;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pl.ki.az.api.RentalSerivce;
import pl.ki.az.api.RentalServiceAPI;
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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BookRentTest {
    private MockBookRepository bookRepository;
    private UserRentalFactory userRentalFactory;
    private RentalSerivce rentalSerivce;
    private MockRentalRepository mockRentalRepository;
    private RentOrders rentOrders;
    private ReturnOrders returnOrders;
    private BookNotExist bookNotExist;

    //TODO dopisać testy kiedy userid nie istnieje w systemie

    //startTestSection
    @BeforeEach
    private void setUp() {
        this.bookRepository = new MockBookRepository();
        this.userRentalFactory = new UserRentalFactory();
        this.mockRentalRepository = new MockRentalRepository();
        this.rentalSerivce = new RentalServiceAPI(mockRentalRepository, bookRepository, userRentalFactory);
        this.rentOrders = new RentOrders();
        this.returnOrders = new ReturnOrders();
    }

    @Test
    @DisplayName("rent an available book")
    void rentTestCase1() {
        //given
        inSystemExistBooks().withId(1L).withId(2L).create();
        client().withId(1L).wouldLikeToRentABookWithId(1L).createRentRequest();

        //when
        rentBooks();

        //then
        rentResult().clientWithId(1L).successfullyRentedBookWithId(1L);
    }

    @Test
    @DisplayName("rent a book that does not exist")
    void rentTestCase2() {
        //given
        inSystemExistBooks().withId(2L).create();
        client().withId(1L).wouldLikeToRentABookWithId(1L).createRentRequest();

        //when
        rentBooks();

        //then
        rentResult().clientWithId(1L)
                .notSuccessfullyRentedBook()
                .becauseBookNotExistInSystem();
    }

    @Test
    @DisplayName("two clients rent two different books")
    void rentTestCase3() {
        //given
        inSystemExistBooks().withId(1L).withId(2L).create();
        client().withId(1L).wouldLikeToRentABookWithId(1L).createRentRequest();
        client().withId(2L).wouldLikeToRentABookWithId(2L).createRentRequest();
        //when
        rentBooks();

        //then
        rentResult().clientWithId(1L)
                .successfullyRentedBookWithId(1L);
        rentResult().clientWithId(2L)
                .successfullyRentedBookWithId(2L);
    }

    @Test
    @DisplayName("two clients rent two different books and books have been rented to right people")
    void rentTestCase4() {
        //given
        inSystemExistBooks().withId(1L).withId(2L).create();
        client().withId(1L).wouldLikeToRentABookWithId(1L).createRentRequest();
        client().withId(2L).wouldLikeToRentABookWithId(2L).createRentRequest();
        //when
        rentBooks();

        //then
        rentResult().clientWithId(1L)
                .successfullyRentedBookWithId(1L)
                .notRentedBookWithId(2L);
        rentResult().clientWithId(2L)
                .successfullyRentedBookWithId(2L)
                .notRentedBookWithId(1L);
    }

    @Test
    @DisplayName("two clients rent the same book")
    void rentTestCase5() {
        //given
        inSystemExistBooks().withId(1L).create();
        client().withId(1L).wouldLikeToRentABookWithId(1L).createRentRequest();
        client().withId(2L).wouldLikeToRentABookWithId(1L).createRentRequest();
        //when
        rentBooks();

        //then
        rentResult().clientWithId(1L)
                .successfullyRentedBookWithId(1L);
        rentResult().clientWithId(2L)
                .notRentedBookWithId(1L)
                .becauseBookIsNotAvailable();
    }

    @Test
    @DisplayName("clients rents a book that he has already rented")
    void rentTestCase6() {
        //given
        inSystemExistBooks().withId(1L).create();
        client().withId(1L).wouldLikeToRentABookWithId(1L).createRentRequest();
        client().withId(1L).wouldLikeToRentABookWithId(1L).createRentRequest();
        //when
        rentBooks();

        //then
        rentResult().clientWithId(1L).rentedOnlyOneBook();
    }

    @Test
    @DisplayName("return a previously rented book")
    void returnTestCase1() {
        //given
        inSystemExistBooks().withId(1L).withId(2L).create();
        alreadyRentedBooks().withId(1L).isRentedToTheUserWithId(1L).create();

        client().withId(1L).wouldLikeToReturnABookWithId(1L).createReturnRequest();

        //when
        returnBooks();

        //then
        returnResult().bookWithId(1L).wasSuccessfullyReturned();
        returnResult().clientWithId(1L).hasNoRentedBooksWithId(1L);
    }

    //TODO ladniej zapisac testCase2 i 3 ??? (chodzi o assercje)
    //TODO może ładniej byłoby skorzystać z Expected Rule?
    @Test
    @DisplayName("return a book that does not exist")
    void returnTestCase2() {
        //given
        inSystemExistBooks().withId(1L).create();
        alreadyRentedBooks().withId(2L).isRentedToTheUserWithId(1L).create();

        client().withId(1L).wouldLikeToReturnABookWithId(2L).createReturnRequest();

        //when
        returnBooksThatNotExist();

        //then
        bookNotExistExceptionWasThrown();
    }

    @Test
    @DisplayName("return a book that was not rented by the customer")
    void returnTestCase3() {
        //given
        inSystemExistBooks().withId(1L).withId(2L).create();
        alreadyRentedBooks().withId(2L).isRentedToTheUserWithId(1L).create();

        client().withId(1L).wouldLikeToReturnABookWithId(1L).createReturnRequest();

        //when
        assertThrows(UsersRentNotFound.class, this::returnBooks);
    }

    @Test
    @DisplayName("customer returns many books")
    void returnTestCase4() {
        //given
        inSystemExistBooks().withId(1L).withId(2L).withId(3L).create();
        alreadyRentedBooks().withId(1L).withId(2L).withId(3L).isRentedToTheUserWithId(1L).create();

        client().withId(1L)
                .wouldLikeToReturnABookWithId(1L).createReturnRequest()
                .wouldLikeToReturnABookWithId(2L).createReturnRequest();

        //when
        returnBooks();

        //then
        returnResult()
                .bookWithId(1L).wasSuccessfullyReturned()
                .bookWithId(2L).wasSuccessfullyReturned();
        returnResult()
                .clientWithId(1L).hasNoRentedBooksWithId(1L)
                .clientWithId(1L).hasNoRentedBooksWithId(2L);
    }


    @Test
    @DisplayName("different customers returns different books")
    void returnTestCase5() {
        //given
        inSystemExistBooks().withId(1L).withId(2L).withId(3L).create();
        alreadyRentedBooks().withId(1L).isRentedToTheUserWithId(1L).create();
        alreadyRentedBooks().withId(2L).withId(3L).isRentedToTheUserWithId(2L).create();

        client().withId(1L)
                .wouldLikeToReturnABookWithId(1L).createReturnRequest();
        client().withId(2L)
                .wouldLikeToReturnABookWithId(2L).createReturnRequest()
                .wouldLikeToReturnABookWithId(3L).createReturnRequest();

        //when
        returnBooks();

        //then
        returnResult()
                .bookWithId(1L).wasSuccessfullyReturned()
                .bookWithId(2L).wasSuccessfullyReturned()
                .bookWithId(3L).wasSuccessfullyReturned();

        returnResult()
                .clientWithId(1L).hasNoRentedBooksWithId(1L)
                .clientWithId(2L).hasNoRentedBooksWithId(2L)
                .clientWithId(2L).hasNoRentedBooksWithId(3L);
    }

    @Test
    @DisplayName("return a book that is not rented by the customer who returns it")
    void returnTestCase6() {
        //given
        inSystemExistBooks().withId(1L).withId(2L).withId(3L).create();
        alreadyRentedBooks().withId(1L).isRentedToTheUserWithId(1L).create();

        client().withId(2L)
                .wouldLikeToReturnABookWithId(1L).createReturnRequest();
        //when
        returnBooks();

        //then
        returnResult()
                .bookWithId(1L).wasSuccessfullyReturned();
        returnResult()
                .clientWithId(1L).hasNoRentedBooksWithId(1L);
    }

    //endTestSection

    private RentedBookAssembler alreadyRentedBooks() {
        return new RentedBookAssembler();
    }

    private void returnBooksThatNotExist() {
        bookNotExist = assertThrows(BookNotExist.class, this::returnBooks);
    }

    private void bookNotExistExceptionWasThrown() {
    }

    private RentResultAssembler rentResult() {
        return new RentResultAssembler();
    }

    private ReturnResultAssembler returnResult() {
        return new ReturnResultAssembler();
    }

    private BookRentAssembler client() {
        return new BookRentAssembler();
    }

    private void rentBooks() {
        rentOrders.executeRentForAllRentOrders();
    }

    private void returnBooks() {
        returnOrders.executeReturnBookForAllReturnOrders();
    }

    //<editor-fold desc="TestAssemblers">
    //startTestAssembler
    private ExistingBookAssembler inSystemExistBooks() {
        return new ExistingBookAssembler();
    }

    private class RentedBookAssembler {

        private List<BookId> rentedBookIds = new ArrayList<>();
        private UserId clientId;

        private RentedBookAssembler withId(Long bookId) {
            this.rentedBookIds.add(new BookId(bookId));
            return this;
        }

        private RentedBookAssembler isRentedToTheUserWithId(Long userId) {
            this.clientId = new UserId(userId);
            return this;
        }

        RentedBookAssembler create() {
            final Client client = new Client(clientId);
            final UserRental userRental = userRentalFactory.create(client);
            rentedBookIds.forEach(bookId -> userRental.rentBook(new Book(bookId)));
            mockRentalRepository.save(userRental);
            return this;
        }
    }

    private class RentResultAssembler {
        private UserId client;

        private RentResultAssembler clientWithId(Long userId) {
            this.client = new UserId(userId);
            return this;
        }

        RentResultAssembler notRentedBookWithId(long bookId) {
            UserRental userRental = mockRentalRepository.loadUsersRent(client);
            boolean isUserRentedSpecificBook = userRental.isUserRentedSpecificBook(new BookId(bookId));
            assertThat(isUserRentedSpecificBook, is(false));
            return this;
        }

        RentResultAssembler successfullyRentedBookWithId(long bookId) {
            RentResult rentResult = rentOrders.getRentResultForUserId(client);
            assertThat(rentResult.isValid(), is(true));

            Book rentedBook = bookRepository.findBookById(new BookId(bookId));
            assertThat(rentedBook.isAvailable(), is(false));
            return this;
        }

        RentResultAssembler notSuccessfullyRentedBook() {
            RentResult rentResult = rentOrders.getRentResultForUserId(client);
            assertThat(rentResult.isValid(), is(false));
            return this;
        }

        RentResultAssembler becauseBookNotExistInSystem() {
            RentResult rentResult = rentOrders.getRentResultForUserId(client);
            assertThat(rentResult.getResult(), is(RentResult.Result.BOOK_NOT_EXIST));
            return this;
        }

        RentResultAssembler becauseBookIsNotAvailable() {
            RentResult rentResult = rentOrders.getRentResultForUserId(client);
            assertThat(rentResult.getResult(), is(RentResult.Result.BOOK_NOT_AVAILABLE));
            return this;
        }

        void rentedOnlyOneBook() {
            ArrayList<RentResult> rentResults = rentOrders.getAllRentResultForUserIdInOrderFromOldestToNewest(client);
            RentResult firstRentRequest = rentResults.get(0);
            RentResult secondRentRequest = rentResults.get(1);
            assertThat(firstRentRequest.isValid(), is(true));
            assertThat(firstRentRequest.getResult(), is(RentResult.Result.SUCCEES));
            assertThat(secondRentRequest.isValid(), is(false));
            assertThat(secondRentRequest.getResult(), is(RentResult.Result.BOOK_NOT_AVAILABLE));
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

        private BookRentAssembler wouldLikeToReturnABookWithId(Long bookId) {
            this.bookId = new BookId(bookId);
            return this;
        }

        private void createRentRequest() {
            final Client client = new Client(userId);
            final UserRental userRental = userRentalFactory.create(client);

            mockRentalRepository.save(userRental);
            rentOrders.addRentOrder(new RentOrder(userId, bookId));
        }

        private BookRentAssembler createReturnRequest() {
            returnOrders.addReturnOrder(new ReturnOrder(bookId));
            return this;
        }
    }

    private class ReturnResultAssembler {
        private UserId userId;
        private BookId bookId;

        private ReturnResultAssembler bookWithId(Long bookId) {
            this.bookId = new BookId(bookId);
            return this;
        }

        ReturnResultAssembler wasSuccessfullyReturned() {
            final ReturnResult returnResult = returnOrders.getReturnResultForBookId(bookId);
            assertThat(returnResult.isValid(), is(true));
            assertThat(returnResult.getResult(), is(ReturnResult.Result.SUCCEES));
            return this;
        }

        ReturnResultAssembler clientWithId(long clientId) {
            this.userId = new UserId(clientId);
            return this;
        }

        ReturnResultAssembler hasNoRentedBooksWithId(long bookId) {
            boolean isUserRentedSearchedBook = mockRentalRepository
                    .loadUsersRent(userId)
                    .isUserRentedSpecificBook(new BookId(bookId));

            assertThat(isUserRentedSearchedBook, is(false));
            return this;
        }
    }

    private class ExistingBookAssembler {
        private List<Book> books = new LinkedList<>();

        private ExistingBookAssembler withId(Long bookId) {
            books.add(new Book(new BookId(bookId)));
            return this;
        }

        void create() {
            bookRepository.addBooks(books);
        }
    }

    //endTestAssembler
    //</editor-fold>
    //<editor-fold desc="SupportingTestClasses">
    //startSupportingTestClasses
    private class ReturnOrders {
        private List<ReturnOrder> returnOrders = new ArrayList<>();

        void addReturnOrder(ReturnOrder returnOrder) {
            this.returnOrders.add(returnOrder);
        }

        void executeReturnBookForAllReturnOrders() {
            this.returnOrders.forEach(ReturnOrder::executeReturnBook);
        }

        ReturnResult getReturnResultForBookId(BookId bookId) {
            return this.returnOrders.stream()
                    .filter(returnOrder -> returnOrder.getBookId().equals(bookId))
                    .findFirst()
                    .get()
                    .getReturnResult();
        }

    }

    private class ReturnOrder {
        private BookId bookId;
        private ReturnResult returnResult;

        ReturnOrder(BookId bookId) {
            this.bookId = bookId;
        }

        void executeReturnBook() {
            this.returnResult = rentalSerivce.returnBook(this.bookId);
        }

        BookId getBookId() {
            return bookId;
        }

        ReturnResult getReturnResult() {
            return returnResult;
        }
    }

    private class RentOrder {
        private UserId userId;
        private BookId bookId;
        private RentResult rentResult;

        RentOrder(UserId userId, BookId bookId) {
            this.userId = userId;
            this.bookId = bookId;
        }

        void executeRentBook() {
            this.rentResult = rentalSerivce.rentBook(this.userId, this.bookId);
        }

        UserId getUserId() {
            return userId;
        }

        RentResult getRentResult() {
            return rentResult;
        }

    }

    private class RentOrders {

        private List<RentOrder> rentOrders = new ArrayList<>();

        void addRentOrder(RentOrder rentOrder) {
            this.rentOrders.add(rentOrder);
        }

        void executeRentForAllRentOrders() {
            rentOrders.forEach(RentOrder::executeRentBook);
        }

        RentResult getRentResultForUserId(UserId userId) {
            return rentOrders.stream()
                    .filter(rentOrder -> rentOrder.getUserId().equals(userId))
                    .findFirst()
                    .get()
                    .getRentResult();
        }

        ArrayList<RentResult> getAllRentResultForUserIdInOrderFromOldestToNewest(UserId userId) {
            return rentOrders.stream()
                    .filter(rentOrder -> rentOrder.getUserId().equals(userId))
                    .collect(Collectors.toList())
                    .stream()
                    .map(RentOrder::getRentResult)
                    .collect(Collectors.toCollection(ArrayList::new));
        }

    }
    //endSupportingTestClasses
    //</editor-fold>
}



