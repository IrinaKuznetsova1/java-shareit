package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NewBooking {
    @NotNull(message = "Дата начала бронирования должна быть указана.")
    @Future(message = "Дата начала бронирования не должна быть прошедшей.")
    private LocalDateTime start;

    @NotNull(message = "Дата окончания бронирования должна быть указана.")
    @Future(message = "Дата окончания бронирования не должна быть прошедшей.")
    private LocalDateTime end;

    @NotNull(message = "itemId должен быть указан.")
    @Positive(message = "itemId должен быть больше нуля.")
    private Long itemId;
}
