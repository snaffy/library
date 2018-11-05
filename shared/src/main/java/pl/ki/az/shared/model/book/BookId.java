package pl.ki.az.shared.model.book;

public class BookId {
    private final Long id;

    public BookId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BookId)) return false;

        BookId bookId = (BookId) o;

        return id != null ? id.equals(bookId.id) : bookId.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
