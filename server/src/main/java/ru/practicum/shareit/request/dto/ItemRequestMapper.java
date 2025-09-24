package ru.practicum.shareit.request.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemRequestMapper {
    public static ItemRequestDto mapToItemRequestDto(ItemRequest itemRequest) {
        return new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                UserMapper.mapToUserDto(itemRequest.getRequestor()),
                itemRequest.getCreated());
    }

    public static ItemRequest mapToItemRequest(NewItemRequest request, User requestor) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(request.getDescription());
        itemRequest.setRequestor(requestor);
        return itemRequest;
    }
}
