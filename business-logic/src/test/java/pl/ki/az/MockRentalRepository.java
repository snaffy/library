package pl.ki.az;


import pl.ki.az.model.client.Client;
import pl.ki.az.model.client.UserId;
import pl.ki.az.rent.UserRental;
import pl.ki.az.repository.RentalRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MockRentalRepository implements RentalRepository {
    private List<UserRental> allUsersRentalList = new ArrayList<>();

    @Override
    public UserRental loadUsersRent(UserId userId) {
        Optional<UserRental> result = allUsersRentalList.stream()
                .filter(userRental -> userRental.getClient().equals(new Client(userId)))
                .findFirst();

        return result.orElse(null);
    }

    @Override
    public void save(UserRental userRental) {
        allUsersRentalList.add(userRental);
    }
}
