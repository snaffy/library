package pl.ki.az.model.book;

public class Book {
    private BookId id;
    private String isbn;
    private String name;
    private Author author;
    private RentalPeriod rentalPeriod;
    private RentStatus rentStatus;

    public Book(BookId id) {
        this.id = id;
        this.rentStatus = RentStatus.AVAILABLE;
    }

    public boolean isAvailable(){
        return RentStatus.AVAILABLE.equals(rentStatus);
    }

    public void changeStatusToRented(){
        this.rentStatus = RentStatus.RENTED;
    }

    public BookId getId() {
        return id;
    }
}
