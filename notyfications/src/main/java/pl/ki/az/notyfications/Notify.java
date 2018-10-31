package pl.ki.az.notyfications;

import java.util.Observer;

public interface Notify {
    void register(Observer obj);

    void unregister(Observer obj);

    void notifyObservers();
}
