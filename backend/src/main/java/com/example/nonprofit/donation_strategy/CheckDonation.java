package com.example.nonprofit.donation_strategy;

import org.springframework.stereotype.Component;

@Component 
public class CheckDonation implements DonationStrategy {

    @Override
    public String getStrategyName() {
        return "CHECK";
    }

    @Override
    public void processDonation() {
        System.out.println("Processing check donation...");
    }
    
}
