package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    List<ItemDto> findItemsByOwnerId(long ownerId);

    ItemDto findById(long itemId);

    ItemDto create(long userId, NewItemRequest newItem);

    ItemDto update(long userId, long id, UpdateItemRequest updItem);

    List<ItemDto> searchItems(String text);
}
