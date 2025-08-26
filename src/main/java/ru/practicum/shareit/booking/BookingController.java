package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBooking;
import ru.practicum.shareit.booking.model.BookingRequestState;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.Collection;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {
    private final BookingService bookingService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public BookingDto create(@RequestHeader("X-Sharer-User-Id") long userId, @Valid @RequestBody NewBooking newBooking) {
        return bookingService.create(userId, newBooking);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto update(@RequestHeader("X-Sharer-User-Id") long userId,
                             @PathVariable @Min(1) long bookingId,
                             @RequestParam @NotNull Boolean approved) {
        return bookingService.update(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto findBookingById(@RequestHeader("X-Sharer-User-Id") long userId,
                                      @PathVariable @Min(1) long bookingId) {
        return bookingService.findById(userId, bookingId);
    }

    @GetMapping
    public Collection<BookingDto> findByBookerId(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @RequestParam(defaultValue = "ALL") BookingRequestState state) {
        return bookingService.findByBookerId(userId, state);
    }

    @GetMapping("/owner")
    public Collection<BookingDto> findByOwnerId(@RequestHeader("X-Sharer-User-Id") long userId,
                                                @RequestParam(defaultValue = "ALL") BookingRequestState state) {
        return bookingService.findByOwnerId(userId, state);
    }


}
