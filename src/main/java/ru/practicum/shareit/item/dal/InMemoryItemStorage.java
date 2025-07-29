package ru.practicum.shareit.item.dal;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class InMemoryItemStorage implements ItemStorage {
    private final Map<Long, Item> items = new HashMap<>();
    private long counter = 0;

    @Override
    public List<Item> getAllByOwnerId(long ownerId) {
        return items.values()
                .stream()
                .filter(item -> item.getOwner().getId() == ownerId)
                .toList();
    }

    @Override
    public Optional<Item> getById(long itemId) {
        return Optional.ofNullable(items.get(itemId));
    }

    @Override
    public Item save(Item item) {
        item.setId(++counter);
        items.put(counter, item);
        return item;
    }

    @Override
    public Item saveUpdatedObject(Item item) {
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public List<Item> searchItems(String text) {
        String str = text.toLowerCase();
        return items.values()
                .stream()
                .filter(item -> (item.getName().toLowerCase().contains(str) ||
                        item.getDescription().toLowerCase().contains(str)) &&
                        item.isAvailable())
                .toList();
    }
}
