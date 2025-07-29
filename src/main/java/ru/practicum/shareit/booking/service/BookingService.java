package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBooking;
import ru.practicum.shareit.booking.model.BookingRequestState;

import java.util.Collection;

public interface BookingService {
    BookingDto create(long userId, NewBooking newBooking);

    BookingDto update(long userId, long bookingId, boolean isApproved);

    BookingDto findById(long userId, long bookingId);

    Collection<BookingDto> findByBookerId(long userId, BookingRequestState state);

    Collection<BookingDto> findByOwnerId(long userId, BookingRequestState state);
}
