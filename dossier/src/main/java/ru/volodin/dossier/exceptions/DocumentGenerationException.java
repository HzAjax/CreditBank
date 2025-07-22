package ru.volodin.dossier.exceptions;

public class DocumentGenerationException extends RuntimeException {
    public DocumentGenerationException(String message) {
        super(message);
    }
}
