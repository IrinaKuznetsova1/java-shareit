package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;

import java.util.List;

public interface ItemService {
    List<ItemWithDatesDto> findItemsByOwnerId(long ownerId);

    ItemWithDatesDto findById(long itemId);

    ItemDto create(long userId, NewItemRequest newItem);

    ItemDto update(long userId, long id, UpdateItemRequest updItem);

    List<ItemDto> searchItems(String text);

    CommentDto createComment(long userId, long itemId, NewCommentRequest newComment);
}
