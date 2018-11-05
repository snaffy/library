package pl.ki.az.notyfications;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.ki.az.notyfications.repository.PendingReservationRepository;
import pl.ki.az.shared.model.book.BookId;
import pl.ki.az.shared.model.client.ClientId;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class TestDump {

    private PendingReservationService pendingReservationService;
    private PendingReservationRepository pendingReservationRepository;
    private MockClientRepository clientRepository = new MockClientRepository();

    @BeforeEach
    void setUp() {
        this.clientRepository = new MockClientRepository();
        this.pendingReservationRepository = new MockPendingReservationRepository();
        this.pendingReservationService = new PendingReservationService(pendingReservationRepository, clientRepository);
    }

    @Test
    void test(){
        //given
        client().withId(1L)
                .waitingForBookWithId(1L)
                .create();

        //when
        books().withId(1L)
                .areAvailableAgain();

        //then
        queueForBook().withId(1L).hasElements(0);
    }

    private class ResultAssembler {
        private BookId bookId;

        ResultAssembler withId(Long bookId) {
            this.bookId = new BookId(bookId);
            return this;
        }

        void hasElements(int exepectedSize) {
            final PendingReservation bookPendingReservation = pendingReservationRepository.loadPendingReservations(bookId);
            assertThat(exepectedSize, is(bookPendingReservation.countPendingClients()));
        }
    }

    private ResultAssembler queueForBook() {
        return new ResultAssembler();
    }

    private class BookAssembler {
        private List<BookId> bookIds = new ArrayList<>();

        BookAssembler withId(Long bookId) {
            bookIds.add(new BookId(bookId));
            return this;
        }

        void areAvailableAgain() {
            bookIds.forEach(bookId -> pendingReservationService.notifyAboutBookAvailability(bookId));
        }

    }

    private BookAssembler books() {
        return new BookAssembler();
    }

    private ClientAssembler client() {
        return new ClientAssembler();
    }


    private class ClientAssembler {
        private ClientId clientId;

        private List<BookId> bookIds = new ArrayList<>();

        private ClientAssembler withId(Long clientId) {
            this.clientId = new ClientId(clientId);
            return this;
        }

        private ClientAssembler waitingForBookWithId(Long bookId) {
            bookIds.add(new BookId(bookId));
            return this;
        }

        void create() {
            clientRepository.addClient(clientId);
            bookIds.forEach(bookId -> pendingReservationService.subscribeToBookAvailability(clientId, bookId));
        }

    }
}
