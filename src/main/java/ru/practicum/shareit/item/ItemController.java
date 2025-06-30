package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public List<ItemDto> findItemsByUserId(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.findItemsByOwnerId(userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto findItemById(@PathVariable @Min(1) long itemId) {
        return itemService.findById(itemId);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") long userId,
                          @Valid @RequestBody NewItemRequest newItem) {
        return itemService.create(userId, newItem);
    }

    @PatchMapping("/{id}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") long userId,
                          @PathVariable @Min(1) long id,
                          @Valid @RequestBody UpdateItemRequest updItem) {
        return itemService.update(userId, id, updItem);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text) {
        return itemService.searchItems(text);
    }

}
