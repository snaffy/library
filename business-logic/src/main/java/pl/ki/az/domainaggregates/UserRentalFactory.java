package pl.ki.az.domainaggregates;

import pl.ki.az.model.client.Client;

public class UserRentalFactory {

    public UserRental create(Client client){

        return new UserRental(client);
    }
}
