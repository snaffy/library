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

    public void changeStatusToAvailable(){
        this.rentStatus = RentStatus.AVAILABLE;
    }

    public BookId getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Book)) return false;

        Book book = (Book) o;

        return id != null ? id.equals(book.id) : book.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
