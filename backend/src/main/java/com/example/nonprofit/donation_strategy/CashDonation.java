package com.example.nonprofit.donation_strategy;

public class CashDonation implements DonationStrategy {
    @Override
    public void processDonation() {
        System.out.println("Processing cash donation...");
    }
    
}
