package pl.ki.az.model.book;

public class Book {
    private BookId id;
    private String isbn;
    private String name;
    private Author author;
    private RentalPeriod rentalPeriod;

    public Book(BookId id) {
        this.id = id;
    }

    public boolean isAvailabe(){
        return true;
    }

}
