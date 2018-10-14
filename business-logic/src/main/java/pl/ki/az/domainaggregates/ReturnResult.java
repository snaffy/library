package pl.ki.az.domainaggregates;

public class ReturnResult {
    private boolean isValid;
    private Result result;

    private ReturnResult(boolean isValid, Result result) {
        this.isValid = isValid;
        this.result = result;
    }

    public static ReturnResult successfullyReturnedBook() {
        return new ReturnResult(true, Result.SUCCEES);
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
