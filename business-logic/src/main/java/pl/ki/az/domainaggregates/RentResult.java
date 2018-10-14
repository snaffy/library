package pl.ki.az.domainaggregates;

public class RentResult {
    private boolean isValid;
    private Result result;

    private RentResult(boolean isValid, Result result) {
        this.isValid = isValid;
        this.result = result;
    }

    static RentResult successfullyRentedBook() {
        return new RentResult(true, Result.SUCCEES);
    }

    static RentResult bookIsNotAvailable() {
        return new RentResult(false, Result.BOOK_NOT_AVAILABLE);
    }

    public static RentResult bookNotExist() {
        return new RentResult(false, Result.BOOK_NOT_EXIST);
    }

    public enum Result {
        SUCCEES, BOOK_NOT_AVAILABLE, BOOK_NOT_EXIST
    }

    public boolean isValid() {
        return isValid;
    }

    public Result getResult() {
        return result;
    }
}
