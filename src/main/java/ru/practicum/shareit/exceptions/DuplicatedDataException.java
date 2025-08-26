package ru.practicum.shareit.exceptions;

import lombok.Getter;

@Getter
public class DuplicatedDataException extends RuntimeException {
    private final String fieldsName;

    public DuplicatedDataException(String fieldsName, String message) {
        super(message);
        this.fieldsName = fieldsName;
    }

}
