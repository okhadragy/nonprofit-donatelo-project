package com.example.nonprofit.document_factory;
import  com.example.nonprofit.models.donations.Donation;
public class DocumentFactory {
    private static final DocumentFactory instance = new DocumentFactory();
    private DocumentFactory() {}
    public static DocumentFactory getInstance() {
        return instance;
    }
    public Document createDocument(Donation donation, String type) {
        if (type.equalsIgnoreCase("tax")) {
            return new TaxDocument(donation);
        } else if (type.equalsIgnoreCase("receipt")) {
            return new ReceiptDocument(donation);
        } else {
            throw new IllegalArgumentException("Invalid document type");
        }
    }
}
