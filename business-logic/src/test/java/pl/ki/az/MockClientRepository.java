package pl.ki.az;


import pl.ki.az.model.client.Client;
import pl.ki.az.model.client.UserId;
import pl.ki.az.repository.ClientRepository;

import java.util.ArrayList;
import java.util.List;

public class MockClientRepository implements ClientRepository {

    private List<Client> existingClients = new ArrayList<>();

    @Override
    public Client loadClientById(UserId userId) {
        return null;
    }

    public void addClient(Client client) {
        existingClients.add(client);
    }
}
