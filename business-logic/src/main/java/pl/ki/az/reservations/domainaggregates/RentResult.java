package pl.ki.az.reservations.domainaggregates;

public class RentResult {
    private final boolean isValid;
    private final Result result;

    private RentResult(boolean isValid, Result result) {
        this.isValid = isValid;
        this.result = result;
    }

    static RentResult successfullyRentedBook() {
        return new RentResult(true, Result.SUCCESS);
    }

    static RentResult bookIsNotAvailable() {
        return new RentResult(false, Result.BOOK_NOT_AVAILABLE);
    }

    public enum Result {
        SUCCESS, BOOK_NOT_AVAILABLE, BOOK_NOT_EXIST
    }

    public boolean isBookNotAvailable(){
        return Result.BOOK_NOT_AVAILABLE == result;
    }

    public boolean isValid() {
        return isValid;
    }

    public Result getResult() {
        return result;
    }
}
