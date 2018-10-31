package pl.ki.az.notyfications.model;

import pl.ki.az.shared.model.client.ClientId;

import java.util.Observable;
import java.util.Observer;

public class Client implements Observer {
    private ClientId clientId;
    private String email;

    public Client(ClientId clientId) {
        this.clientId = clientId;
    }

    @Override
    public void update(Observable observable, Object o) {

    }
}
