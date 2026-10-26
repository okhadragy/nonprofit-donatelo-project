package com.example.nonprofit.donation_strategy;

public class CheckDonation implements DonationStrategy {
    @Override
    public void processDonation() {
        System.out.println("Processing check donation...");
    }
    
}
