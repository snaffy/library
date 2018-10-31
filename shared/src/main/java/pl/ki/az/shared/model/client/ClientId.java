package pl.ki.az.shared.model.client;

public class ClientId {

    private final Long clientId;

    public ClientId(Long clientId) {
        this.clientId = clientId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClientId)) return false;

        ClientId clientId1 = (ClientId) o;

        return clientId != null ? clientId.equals(clientId1.clientId) : clientId1.clientId == null;
    }

    @Override
    public int hashCode() {
        return clientId != null ? clientId.hashCode() : 0;
    }
}
