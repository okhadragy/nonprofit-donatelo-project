package com.example.nonprofit.event_observer;

public interface Subject {
    void addObserver(EventObserver observer);
    void removeObserver(EventObserver observer);
    void notifyObservers();
}

