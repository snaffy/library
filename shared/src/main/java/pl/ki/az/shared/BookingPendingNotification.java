package pl.ki.az.shared;

import pl.ki.az.shared.model.client.ClientId;

public interface BookingPendingNotification {
    void subscribeToBookAvailability(ClientId clientId);
}
