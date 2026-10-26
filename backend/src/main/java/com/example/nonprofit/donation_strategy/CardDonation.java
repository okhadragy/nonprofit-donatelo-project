package com.example.nonprofit.donation_strategy;

public class CardDonation implements DonationStrategy {
    @Override
    public void processDonation() {
        System.out.println("Processing card donation...");
    }
    
}