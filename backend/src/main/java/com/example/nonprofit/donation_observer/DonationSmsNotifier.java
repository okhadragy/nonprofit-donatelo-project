package com.example.nonprofit.donation_observer;

import org.springframework.stereotype.Component;

@Component
public class DonationSmsNotifier implements DonationObserver {
    @Override
    public void update(String message) {
        System.out.println("SMS sent: " + message);
    }
}
