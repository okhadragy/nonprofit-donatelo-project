package com.example.nonprofit.document_factory;

import com.example.nonprofit.models.donations.Donation;

public class ReceiptDocumentFactory extends DocumentFactory {
    private static final ReceiptDocumentFactory INSTANCE = new ReceiptDocumentFactory();

    private ReceiptDocumentFactory() {
    }

    public static ReceiptDocumentFactory getInstance() {
        return INSTANCE;
    }

    @Override
    public Document createDocument(Donation donation) {
        return new ReceiptDocument(donation);
    }
}
