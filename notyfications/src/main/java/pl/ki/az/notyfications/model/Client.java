package pl.ki.az.notyfications.model;

import pl.ki.az.shared.model.client.ClientId;

public class Client {
    private ClientId clientId;
    private String email;
    private String name;
    private String surname;

    public Client(ClientId clientId) {
        this.clientId = clientId;
    }

    public ClientId getClientId() {
        return clientId;
    }
}
