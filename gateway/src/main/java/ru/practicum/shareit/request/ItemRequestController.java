package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.NewItemRequest;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @Valid @RequestBody NewItemRequest newItemRequest) {
        return itemRequestClient.create(userId, newItemRequest);
    }

    @GetMapping
    public ResponseEntity<Object> findItemRequestsByUserId(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemRequestClient.findItemRequestsByUserId(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findOtherUsersItemRequests(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemRequestClient.findOtherUsersItemRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> findItemRequestById(@PathVariable @Min(1) long requestId) {
        return itemRequestClient.findById(requestId);
    }

}
