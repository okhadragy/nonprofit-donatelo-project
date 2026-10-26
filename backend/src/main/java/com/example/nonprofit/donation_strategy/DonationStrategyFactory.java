package com.example.nonprofit.donation_strategy;

public class DonationStrategyFactory {
    private static DonationStrategyFactory instance;

    public static DonationStrategyFactory getInstance() {
        if (instance == null) {
            instance = new DonationStrategyFactory();
        }
        return instance;
    }

    private DonationStrategyFactory() {
    }

    public DonationStrategy getDonationStrategy(String type) {
        type = type.toUpperCase();
        if (type.equals("CASH")) {
            return new CashDonation();
        } else if (type.equals("CHECK")) {
            return new CheckDonation();
        } else if (type.equals("CARD")) {
            return new CardDonation();
        }
        return null;
    }

}
