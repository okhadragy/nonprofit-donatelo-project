package com.example.nonprofit.donation_strategy;

import org.springframework.stereotype.Component;

@Component 
public class CardDonation implements DonationStrategy {

    @Override
    public String getStrategyName() {
        return "CARD";
    }

    @Override
    public void processDonation() {
        System.out.println("Processing card donation...");
    }
    
}