package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dal.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.request.dal.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dto.NewItemRequest;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dal.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequestDto create(long userId, NewItemRequest newItemRequest) {
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id - " + userId + " не найден."));
        final ItemRequest savedItemRequest = itemRequestRepository.save(ItemRequestMapper.mapToItemRequest(newItemRequest, user));
        return ItemRequestMapper.mapToItemRequestDto(savedItemRequest);
    }

    @Override
    public List<ItemRequestDto> findItemRequestsByUserId(long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id - " + userId + " не найден."));
        Sort newestFirst = Sort.by(Sort.Direction.DESC, "created");
        return itemRequestRepository.findByRequestorId(userId, newestFirst)
                .stream()
                .map(itemRequest -> {
                    ItemRequestDto itemRequestDto = ItemRequestMapper.mapToItemRequestDto(itemRequest);
                    Set<ItemDto> itemsDto = itemRepository.findByItemRequestId(itemRequest.getId())
                            .stream()
                            .map(ItemMapper::mapToItemDto)
                            .collect(Collectors.toSet());
                    itemRequestDto.setItems(itemsDto);
                    return itemRequestDto;
                })
                .toList();
    }

    @Override
    public List<ItemRequestDto> findOtherUsersItemRequests(long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id - " + userId + " не найден."));
        return itemRequestRepository.findByRequestorIdNot(userId, Sort.by(Sort.Direction.DESC, "created"))
                .stream()
                .map(ItemRequestMapper::mapToItemRequestDto)
                .toList();
    }

    @Override
    public ItemRequestDto findById(long requestId) {
        final ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос с id - " + requestId + " не найден."));
        final ItemRequestDto itemRequestDto = ItemRequestMapper.mapToItemRequestDto(itemRequest);
        itemRequestDto.setItems(itemRepository.findByItemRequestId(itemRequest.getId())
                .stream()
                .map(ItemMapper::mapToItemDto)
                .collect(Collectors.toSet()));
        return itemRequestDto;
    }


}
