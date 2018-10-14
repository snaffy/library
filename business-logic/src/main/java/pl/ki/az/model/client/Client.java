package pl.ki.az.model.client;

public class Client {

    private UserId userId;
    private String name;
    private String surname;

    public Client(UserId userId) {
        this.userId = userId;

    }

    @Override
    public int hashCode() {
        return userId != null ? userId.hashCode() : 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Client)) return false;

        Client client = (Client) o;

        return userId != null ? userId.equals(client.userId) : client.userId == null;
    }
}
