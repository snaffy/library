package pl.ki.az.repository;

import org.springframework.stereotype.Repository;
import pl.ki.az.model.client.UserId;
import pl.ki.az.rent.UserRental;

@Repository
public interface RentalRepository {
    UserRental loadUsersRent(UserId userId);

    void save(UserRental userRental);
}
