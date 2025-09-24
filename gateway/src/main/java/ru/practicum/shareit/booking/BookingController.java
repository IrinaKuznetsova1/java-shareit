package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.NewBooking;
import ru.practicum.shareit.booking.dto.BookingRequestState;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @Valid @RequestBody NewBooking newBooking) {
        return bookingClient.create(userId, newBooking);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> update(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @PathVariable @Min(1) long bookingId,
                                         @RequestParam @NotNull Boolean approved) {
        return bookingClient.approveBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> findBookingById(@RequestHeader("X-Sharer-User-Id") long userId,
                                                  @PathVariable @Min(1) long bookingId) {
        return bookingClient.findBookingById(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> findByBookerId(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @RequestParam(defaultValue = "ALL") BookingRequestState state) {
        return bookingClient.findByBookerId(userId, state);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> findByOwnerId(@RequestHeader("X-Sharer-User-Id") long userId,
                                                @RequestParam(defaultValue = "ALL") BookingRequestState state) {
        return bookingClient.findByOwnerId(userId, state);
    }
}
