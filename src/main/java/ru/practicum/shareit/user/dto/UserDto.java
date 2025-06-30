package ru.practicum.shareit.user.dto;

import lombok.Data;

@Data
public class UserDto {
    private final long id;
    private final String email;
    private final String name;
}
