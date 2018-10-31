package pl.ki.az.reservations;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pl.ki.az.reservations.api.ReservationService;
import pl.ki.az.reservations.api.ReservationServiceAPI;
import pl.ki.az.reservations.domainaggregates.BookReservation;
import pl.ki.az.reservations.domainaggregates.RentResult;
import pl.ki.az.reservations.domainaggregates.ReturnResult;
import pl.ki.az.reservations.exceptions.BookReservationNotFound;
import pl.ki.az.shared.model.book.BookId;
import pl.ki.az.shared.model.client.ClientId;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BookRentTest {

    private ReservationService reservationService;
    private MockReservationRepository mockReservationRepository;
    private RentOrders rentOrders;
    private ReturnOrders returnOrders;


    //TODO dopisać testy kiedy userid nie istnieje w systemie

    //startTestSection
    @BeforeEach
    void setUp() {
        this.mockReservationRepository = new MockReservationRepository();
        this.reservationService = new ReservationServiceAPI(mockReservationRepository, new MockBookingPendingNotification());
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

        //when //then
        assertThrows(BookReservationNotFound.class, this::rentBooks);
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

    @Test
    @DisplayName("return a book that does not exist")
    void returnTestCase2() {
        //given
        inSystemExistBooks().withId(1L).create();
        alreadyRentedBooks().withId(1L).isRentedToTheUserWithId(1L).create();

        client().withId(1L).wouldLikeToReturnABookWithId(2L).createReturnRequest();

        //when //then
        assertThrows(BookReservationNotFound.class, this::returnBooks);

    }

    //
    @Test
    @DisplayName("return a book that was not rented by the customer")
    void returnTestCase3() {
        //given
        inSystemExistBooks().withId(1L).withId(2L).create();
        alreadyRentedBooks().withId(2L).isRentedToTheUserWithId(1L).create();

        client().withId(1L).wouldLikeToReturnABookWithId(1L).createReturnRequest();

        //when
        returnBooks();

        //then
        returnResult().bookWithId(1L).wasNotSuccessfullyReturned();
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

        private final List<BookId> rentedBookIds = new ArrayList<>();
        private ClientId clientId;

        private RentedBookAssembler withId(Long bookId) {
            this.rentedBookIds.add(new BookId(bookId));
            return this;
        }

        private RentedBookAssembler isRentedToTheUserWithId(Long clientId) {
            this.clientId = new ClientId(clientId);
            return this;
        }

        void create() {
            rentedBookIds.forEach(bookId -> {
                BookReservation bookReservation = mockReservationRepository.loadBooksReservation(bookId);
                bookReservation.rentBookForClient(clientId);
            });
        }
    }

    private class RentResultAssembler {
        private ClientId clientId;

        private RentResultAssembler clientWithId(Long clientId) {
            this.clientId = new ClientId(clientId);
            return this;
        }

        RentResultAssembler notRentedBookWithId(long bookId) {
            BookReservation bookReservation = mockReservationRepository.loadBooksReservation(new BookId(bookId));
            boolean bookBorrowedByClient = bookReservation.isBookBorrowedByClient(clientId);
            assertThat(bookBorrowedByClient, is(false));
            return this;
        }

        RentResultAssembler successfullyRentedBookWithId(long bookId) {
            RentResult rentResult = rentOrders.getRentResultForUserId(clientId);
            assertThat(rentResult.isValid(), is(true));
            return this;
        }

        RentResultAssembler notSuccessfullyRentedBook() {
            RentResult rentResult = rentOrders.getRentResultForUserId(clientId);
            assertThat(rentResult.isValid(), is(false));
            return this;
        }

        RentResultAssembler becauseBookNotExistInSystem() {
            RentResult rentResult = rentOrders.getRentResultForUserId(clientId);
            assertThat(rentResult.getResult(), is(RentResult.Result.BOOK_NOT_EXIST));
            return this;
        }

        void becauseBookIsNotAvailable() {
            RentResult rentResult = rentOrders.getRentResultForUserId(clientId);
            assertThat(rentResult.getResult(), is(RentResult.Result.BOOK_NOT_AVAILABLE));
        }

        void rentedOnlyOneBook() {
            ArrayList<RentResult> rentResults = rentOrders.getAllRentResultForUserIdInOrderFromOldestToNewest(clientId);
            RentResult firstRentRequest = rentResults.get(0);
            RentResult secondRentRequest = rentResults.get(1);
            assertThat(firstRentRequest.isValid(), is(true));
            assertThat(firstRentRequest.getResult(), is(RentResult.Result.SUCCESS));
            assertThat(secondRentRequest.isValid(), is(false));
            assertThat(secondRentRequest.getResult(), is(RentResult.Result.BOOK_NOT_AVAILABLE));
        }

    }

    private class BookRentAssembler {
        private ClientId clientId;
        private BookId bookId;

        private BookRentAssembler withId(Long clientId) {
            this.clientId = new ClientId(clientId);
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
//            final BookReservation bookReservation = new BookReservation(bookId);
//            mockReservationRepository.save(bookReservation);

            rentOrders.addRentOrder(new RentOrder(clientId, bookId));
        }

        private BookRentAssembler createReturnRequest() {
            returnOrders.addReturnOrder(new ReturnOrder(bookId));
            return this;
        }
    }

    private class ReturnResultAssembler {
        private ClientId clientId;
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

        void wasNotSuccessfullyReturned() {
            final ReturnResult returnResult = returnOrders.getReturnResultForBookId(bookId);
            assertThat(returnResult.isValid(), is(false));
            assertThat(returnResult.getResult(), is(ReturnResult.Result.FAILURE));
        }

        ReturnResultAssembler clientWithId(long clientId) {
            this.clientId = new ClientId(clientId);
            return this;
        }

        ReturnResultAssembler hasNoRentedBooksWithId(long bookId) {
            boolean isUserRentedSearchedBook = mockReservationRepository
                    .loadBooksReservation(new BookId(bookId))
                    .isBookBorrowedByClient(this.clientId);

            assertThat(isUserRentedSearchedBook, is(false));
            return this;
        }
    }

    private class ExistingBookAssembler {
        private final List<BookReservation> existingBookReservations = new LinkedList<>();

        private ExistingBookAssembler withId(Long bookId) {
            existingBookReservations.add(new BookReservation(new BookId(bookId)));
            return this;
        }

        void create() {
            mockReservationRepository.saveAllReservations(existingBookReservations);
        }
    }

    //endTestAssembler
    //</editor-fold>
    //<editor-fold desc="SupportingTestClasses">
    //startSupportingTestClasses
    private class ReturnOrders {
        private final List<ReturnOrder> returnOrders = new ArrayList<>();

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
        private final BookId bookId;
        private ReturnResult returnResult;

        ReturnOrder(BookId bookId) {
            this.bookId = bookId;
        }

        void executeReturnBook() {
            this.returnResult = reservationService.returnBook(this.bookId);
        }

        BookId getBookId() {
            return bookId;
        }

        ReturnResult getReturnResult() {
            return returnResult;
        }
    }

    private class RentOrder {
        private final ClientId clientId;
        private final BookId bookId;
        private RentResult rentResult;

        RentOrder(ClientId clientId, BookId bookId) {
            this.clientId = clientId;
            this.bookId = bookId;
        }

        void executeRentBook() {
            this.rentResult = reservationService.rentBook(this.clientId, this.bookId);
        }

        ClientId getUserId() {
            return clientId;
        }

        RentResult getRentResult() {
            return rentResult;
        }

    }

    private class RentOrders {

        private final List<RentOrder> rentOrders = new ArrayList<>();

        void addRentOrder(RentOrder rentOrder) {
            this.rentOrders.add(rentOrder);
        }

        void executeRentForAllRentOrders() {
            rentOrders.forEach(RentOrder::executeRentBook);
        }

        RentResult getRentResultForUserId(ClientId clientId) {
            return rentOrders.stream()
                    .filter(rentOrder -> rentOrder.getUserId().equals(clientId))
                    .findFirst()
                    .get()
                    .getRentResult();
        }

        ArrayList<RentResult> getAllRentResultForUserIdInOrderFromOldestToNewest(ClientId clientId) {
            return rentOrders.stream()
                    .filter(rentOrder -> rentOrder.getUserId().equals(clientId))
                    .collect(Collectors.toList())
                    .stream()
                    .map(RentOrder::getRentResult)
                    .collect(Collectors.toCollection(ArrayList::new));
        }

    }
    //endSupportingTestClasses
    //</editor-fold>
}



