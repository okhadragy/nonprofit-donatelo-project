package com.example.nonprofit.donation_observer;

public interface Subject {
    public void addObserver(DonationObserver observer);
    public void removeObserver(DonationObserver observer);
    public void notifyObservers();
}
