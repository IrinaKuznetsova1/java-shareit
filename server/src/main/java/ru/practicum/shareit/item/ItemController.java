package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public List<ItemWithDatesDto> findItemsByUserId(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.findItemsByOwnerId(userId);
    }

    @GetMapping("/{itemId}")
    public ItemWithDatesDto findItemById(@PathVariable long itemId) {
        return itemService.findById(itemId);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") long userId,
                          @RequestBody NewItem newItem) {
        return itemService.create(userId, newItem);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                    @PathVariable long itemId,
                                    @RequestBody NewCommentRequest newComment) {
        return itemService.createComment(userId, itemId, newComment);
    }

    @PatchMapping("/{id}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") long userId,
                          @PathVariable long id,
                          @RequestBody UpdateItemRequest updItem) {
        return itemService.update(userId, id, updItem);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text) {
        return itemService.searchItems(text);
    }
}
