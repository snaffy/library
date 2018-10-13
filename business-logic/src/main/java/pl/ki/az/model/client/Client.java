package pl.ki.az.model.client;

public class Client {

    private UserId userId;
    private String name;
    private String surname;

    public Client(UserId userId, String name, String surname) {
        this.userId = userId;
        this.name = name;
        this.surname = surname;
    }
}
