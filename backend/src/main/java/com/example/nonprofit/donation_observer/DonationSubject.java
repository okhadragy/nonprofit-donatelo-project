    package com.example.nonprofit.donation_observer;
    import java.util.ArrayList;
    import java.util.List;

    import org.springframework.stereotype.Component;

    @Component
    public class DonationSubject implements Subject {
        private final List<DonationObserver> observers= new ArrayList<>();
        private String message;

        public DonationSubject(List<DonationObserver> observers) {
            this.observers.addAll(observers);
            
        }

        @Override
        public void addObserver(DonationObserver observer) {
            observers.add(observer);
        }

        @Override
        public void removeObserver(DonationObserver observer) {
            observers.remove(observer);
        }

        @Override
        public void notifyObservers() {
            for (DonationObserver observer : observers) {
                observer.update(message);
            }
        }

        public void setMessage(String message) {
            this.message = message;
            notifyObservers();
        }
        
    }
