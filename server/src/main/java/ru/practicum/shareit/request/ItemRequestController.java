package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;


@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ItemRequestDto create(@RequestHeader("X-Sharer-User-Id") long userId,
                                 @RequestBody NewItemRequest newItemRequest) {
        return itemRequestService.create(userId, newItemRequest);
    }

    @GetMapping
    public List<ItemRequestDto> findItemRequestsByUserId(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemRequestService.findItemRequestsByUserId(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> findOtherUsersItemRequests(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemRequestService.findOtherUsersItemRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto findItemRequestById(@PathVariable long requestId) {
        return itemRequestService.findById(requestId);
    }
}
