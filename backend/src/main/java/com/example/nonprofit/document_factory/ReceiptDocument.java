package com.example.nonprofit.document_factory;
import  com.example.nonprofit.models.donations.Donation;


public class ReceiptDocument implements Document {
    private final Donation donation;
    public ReceiptDocument(Donation donation) {
        this.donation = donation;
    }
    @Override 
    public String generate() {
        return "Receipt Document for Donation\n" +
               "Donor Name: " + donation.getDonor().getName() + "\n" +
               "Donation Amount: $" + donation.getAmount() + "\n" +
               "Date: " + donation.getDate() + "\n" +
               "Donation Method: " + donation.getMethod() + "\n" +
               "Donation Type: " + donation.getDonationType().getName();
    }
}
