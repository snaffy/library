package pl.ki.az.notyfications;

import pl.ki.az.notyfications.model.Client;
import pl.ki.az.notyfications.repository.ClientRepository;
import pl.ki.az.shared.model.client.ClientId;

public class NotificationService {

    private ClientRepository clientRepository;

    NotificationService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    void notifyClientAboutBookAvailability(ClientId clientId) {
        final Client clientDetails = clientRepository.findClientDetails(clientId);

        if(clientDetails == null){
            throw new ClientNotExist();
        }

        //send email about book availability...
    }
}
