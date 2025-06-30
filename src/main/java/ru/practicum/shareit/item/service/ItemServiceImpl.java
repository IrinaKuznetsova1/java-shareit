package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeptoins.NotFoundException;
import ru.practicum.shareit.item.dal.ItemStorage;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserService userService;

    @Override
    public List<ItemDto> findItemsByOwnerId(long ownerId) {
        userService.findById(ownerId);
        return itemStorage.getAllByOwnerId(ownerId)
                .stream()
                .map(ItemMapper::mapToItemDto)
                .toList();
    }

    @Override
    public ItemDto findById(long itemId) {
        final Item item = itemStorage.getById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id - " + itemId + " не найдена."));
        return ItemMapper.mapToItemDto(item);
    }

    @Override
    public ItemDto create(long userId, NewItemRequest newItem) {
        userService.findById(userId);
        final Item item = ItemMapper.mapToItem(newItem);
        item.setOwner(userId);
        final Item itemWithId = itemStorage.save(item);
        return ItemMapper.mapToItemDto(itemWithId);
    }

    @Override
    public ItemDto update(long userId, long id, UpdateItemRequest updItem) {
        userService.findById(userId);
        final Item oldItem = itemStorage.getById(id)
                .orElseThrow(() -> new NotFoundException("Вещь с id - " + id + " не найдена."));
        if (oldItem.getOwner() != userId)
            throw new NotFoundException("Вещь id - " + id + " пользователя id - " + userId + " не найдена.");
        final Item itemAfterUpdate = ItemMapper.updateItemFields(oldItem, updItem);
        return ItemMapper.mapToItemDto(itemStorage.saveUpdatedObject(itemAfterUpdate));
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text == null || text.isBlank()) return Collections.emptyList();
        return itemStorage.searchItems(text)
                .stream()
                .map(ItemMapper::mapToItemDto)
                .toList();
    }
}
