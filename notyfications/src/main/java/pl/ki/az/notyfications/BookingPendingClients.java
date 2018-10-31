package pl.ki.az.notyfications;


import java.util.ArrayList;
import java.util.List;
import java.util.Observer;

public class BookingPendingClients implements Notify {
    private List<Observer> pendingClients = new ArrayList<>();

    @Override
    public void register(Observer observer) {
        pendingClients.add(observer);
    }

    @Override
    public void unregister(Observer observer) {
        pendingClients.remove(observer);
    }

    @Override
    public void notifyObservers() {

    }
}
