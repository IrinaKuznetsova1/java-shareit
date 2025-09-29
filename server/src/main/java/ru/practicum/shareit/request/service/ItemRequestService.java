package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequest;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto create(long userId, NewItemRequest newItemRequest);

    List<ItemRequestDto> findItemRequestsByUserId(long userId);

    List<ItemRequestDto> findOtherUsersItemRequests(long userId);

    ItemRequestDto findById(long requestId);
}
