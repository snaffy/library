package book;

import java.time.LocalDateTime;

public class RentalPeriod {
    private LocalDateTime startDateOfRent;
    private LocalDateTime endDateOfRent;

    public RentalPeriod(LocalDateTime startDateOfRent, LocalDateTime endDateOfRent) {
        this.startDateOfRent = startDateOfRent;
        this.endDateOfRent = endDateOfRent;
    }
}
