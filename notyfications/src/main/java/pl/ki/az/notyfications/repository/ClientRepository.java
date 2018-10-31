package pl.ki.az.notyfications.repository;

import pl.ki.az.notyfications.model.Client;
import pl.ki.az.shared.model.client.ClientId;

public interface ClientRepository {
    Client findClientDetails(ClientId clientId);
}
