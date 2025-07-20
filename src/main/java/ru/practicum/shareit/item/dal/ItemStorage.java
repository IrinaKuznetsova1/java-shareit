package ru.practicum.shareit.item.dal;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemStorage {
    List<Item> getAllByOwnerId(long ownerId);

    Optional<Item> getById(long itemId);

    Item save(Item item);

    Item saveUpdatedObject(Item item);

    List<Item> searchItems(String text);
}
