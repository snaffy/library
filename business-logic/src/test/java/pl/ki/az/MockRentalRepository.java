package pl.ki.az;


import pl.ki.az.model.client.UserId;
import pl.ki.az.rent.UserRental;
import pl.ki.az.repository.RentalRepository;

import java.util.ArrayList;
import java.util.List;

public class MockRentalRepository implements RentalRepository {
    private List<UserRental> allUsersRentalList = new ArrayList<>();

    @Override
    public UserRental loadUsersRent(UserId userId) {
        return null;
    }

    @Override
    public void save(UserRental userRental) {
        allUsersRentalList.add(userRental);
    }
}
