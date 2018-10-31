package pl.ki.az.notyfications;

import pl.ki.az.shared.model.client.ClientId;

import java.time.LocalDateTime;

public class PendingClient implements Comparable<LocalDateTime> {
    private ClientId clientId;
    private LocalDateTime startPendingForBookDate;

    PendingClient(ClientId clientId) {
        this.clientId = clientId;
        this.startPendingForBookDate = LocalDateTime.now();
    }

    ClientId getClientId() {
        return clientId;
    }

    @Override
    public int compareTo(LocalDateTime localDateTime) {
        return startPendingForBookDate.compareTo(localDateTime);
    }
}
