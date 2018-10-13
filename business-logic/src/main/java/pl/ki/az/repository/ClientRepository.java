package pl.ki.az.repository;

import pl.ki.az.model.client.Client;
import pl.ki.az.model.client.UserId;

public interface ClientRepository {
    Client loadClientById(UserId userId);
}
