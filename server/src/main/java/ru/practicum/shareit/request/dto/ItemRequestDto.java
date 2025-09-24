package ru.practicum.shareit.request.dto;

import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class ItemRequestDto {
    private final long id;
    private final String description;
    private final UserDto requestor;
    private final LocalDateTime created;
    private Set<ItemDto> items;
}
