package com.example.nonprofit.document_factory;

import com.example.nonprofit.models.donations.Donation;

public abstract class DocumentFactory {
    public abstract Document createDocument(Donation donation);
}
