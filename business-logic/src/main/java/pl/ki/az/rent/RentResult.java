package pl.ki.az.rent;

public class RentResult {
    private boolean isValid;
    private Result result;

    private RentResult(boolean isValid, Result result) {
        this.isValid = isValid;
        this.result = result;
    }

    public static RentResult successfullyRentedBook() {
        return new RentResult(true, Result.SUCCEES);
    }

    public static RentResult bookIsNotAvailable() {
        return new RentResult(false, Result.BOOK_NOT_AVAILABLE);
    }

    public static RentResult bookNotExist() {
        return new RentResult(false, Result.BOOK_NOT_EXIST);
    }


    private enum Result {
        SUCCEES, BOOK_NOT_AVAILABLE, BOOK_NOT_EXIST
    }


}
