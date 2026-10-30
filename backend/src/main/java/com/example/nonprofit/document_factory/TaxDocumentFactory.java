package com.example.nonprofit.document_factory;

import com.example.nonprofit.models.donations.Donation;

public class TaxDocumentFactory extends DocumentFactory {
    private static final TaxDocumentFactory INSTANCE = new TaxDocumentFactory();

    private TaxDocumentFactory() {
    }

    public static TaxDocumentFactory getInstance() {
        return INSTANCE;
    }

    @Override
    public Document createDocument(Donation donation) {
        return new TaxDocument(donation);
    }
}
