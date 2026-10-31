package com.example.nonprofit.donation_observer;

import org.springframework.stereotype.Component;

@Component
public class DonationInSiteNotifier implements DonationObserver {
    @Override
    public void update(String message) {
        System.out.println("In-site notification sent: " + message);
    }
}
