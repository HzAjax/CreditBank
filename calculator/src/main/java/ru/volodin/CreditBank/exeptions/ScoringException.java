package ru.volodin.CreditBank.exeptions;

public class ScoringException extends RuntimeException {
    public ScoringException(String message) {
        super(message);
    }
}
