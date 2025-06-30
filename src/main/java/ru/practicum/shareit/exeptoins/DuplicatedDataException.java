package ru.practicum.shareit.exeptoins;

import lombok.Getter;

@Getter
public class DuplicatedDataException extends RuntimeException {
    private final String fieldsName;

    public DuplicatedDataException(String fieldsName, String message) {
        super(message);
        this.fieldsName = fieldsName;
    }

}
