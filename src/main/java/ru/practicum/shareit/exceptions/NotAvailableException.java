package ru.practicum.shareit.exceptions;

import lombok.Getter;

@Getter
public class NotAvailableException extends RuntimeException {
    private final String fieldNameWithError;
    public NotAvailableException(String fieldNameWithError, String message) {
        super(message);
        this.fieldNameWithError = fieldNameWithError;
    }
}
