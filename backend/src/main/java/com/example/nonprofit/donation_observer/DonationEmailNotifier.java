package com.example.nonprofit.donation_observer;

import org.springframework.stereotype.Component;

@Component
public class DonationEmailNotifier implements DonationObserver {
    @Override
    public void update(String message) {
        System.out.println("Email sent: " + message);
    }
}