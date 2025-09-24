package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;

import java.util.Collections;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> findItemsByUserId(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemClient.findItemsByOwnerId(userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> findItemById(@PathVariable @Min(1) long itemId) {
        return itemClient.findById(itemId);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @Valid @RequestBody NewItem newItem) {
        return itemClient.create(userId, newItem);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                                @PathVariable @Min(1) long itemId,
                                                @Valid @RequestBody NewCommentRequest newComment) {
        return itemClient.createComment(userId, itemId, newComment);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @PathVariable @Min(1) long id,
                                         @Valid @RequestBody UpdateItemRequest updItem) {
        return itemClient.update(userId, id, updItem);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam String text) {
        if (text == null || text.isBlank()) return ResponseEntity.ok(Collections.emptyList());
        return itemClient.searchItems(text);
    }
}
