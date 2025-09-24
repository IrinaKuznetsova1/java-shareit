package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

@Data
public class BookingDto {
    private final long id;
    private final LocalDateTime start;
    private final LocalDateTime end;
    private final ItemDto item;
    private final UserDto booker;
    private final BookingStatus status;
}
