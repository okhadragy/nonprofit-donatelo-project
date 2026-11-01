package com.example.nonprofit.donation_strategy;

import org.springframework.stereotype.Component;

@Component 
public class CashDonation implements DonationStrategy {

    @Override 
    public String getStrategyName() {
        return "CASH";
    }

    @Override
    public void processDonation() {
        System.out.println("Processing cash donation...");
    }
    
}
