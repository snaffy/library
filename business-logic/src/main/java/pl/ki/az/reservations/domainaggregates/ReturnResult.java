package pl.ki.az.reservations.domainaggregates;

public class ReturnResult {
    private final boolean isValid;
    private final Result result;

    private ReturnResult(boolean isValid, Result result) {
        this.isValid = isValid;
        this.result = result;
    }

    static ReturnResult successfullyReturnedBook() {
        return new ReturnResult(true, Result.SUCCEES);
    }

    static ReturnResult canNotReturnABookThatHasNotBeenBorrowedBefore() {
        return new ReturnResult(false, Result.FAILURE);
    }

    public enum Result {
        SUCCEES, FAILURE
    }

    public boolean isValid(){
        return isValid;
    }

    public Result getResult() {
        return result;
    }
}
