package pl.ki.az.notyfications;

import pl.ki.az.notyfications.model.Client;
import pl.ki.az.notyfications.repository.ClientRepository;
import pl.ki.az.shared.model.client.ClientId;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MockClientRepository implements ClientRepository {

    private List<Client> clientList = new ArrayList<>();

    @Override
    public Client findClientDetails(ClientId clientId) {
        Optional<Client> foundClient = clientList.stream()
                .filter(client -> client.getClientId().equals(clientId))
                .findFirst();

        return foundClient.orElse(null);
    }

    public void addClient(ClientId clientId){
        clientList.add(new Client(clientId));
    }
}
