package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dal.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exceptions.NotAvailableException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dal.CommentRepository;
import ru.practicum.shareit.item.dal.ItemRepository;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dal.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public List<ItemWithDatesDto> findItemsByOwnerId(long ownerId) {
        userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id - " + ownerId + " не найден."));
        final List<ItemWithDatesDto> items = itemRepository.findByOwnerId(ownerId)
                .stream()
                .map(ItemMapper::mapToItemWithDatesDto)
                .toList();
        final List<Booking> bookings = bookingRepository.findByItemOwnerId(ownerId, Sort.by(Sort.Direction.DESC, "start"));

        for (ItemWithDatesDto itemWithDatesDto : items) {
            itemWithDatesDto.setComments(
                    commentRepository.findByItemIdOrderByCreatedDesc(itemWithDatesDto.getId())
                            .stream()
                            .map(CommentMapper::mapToCommentDto)
                            .toList());
            bookings.stream()
                    .filter(booking -> booking.getItem().getId() == itemWithDatesDto.getId() && booking.getEnd().isBefore(LocalDateTime.now()))
                    .findFirst()
                    .ifPresent(booking -> itemWithDatesDto.setLastBooking(booking.getStart()));
            bookings.stream()
                    .filter(booking -> booking.getItem().getId() == itemWithDatesDto.getId() && booking.getStart().isAfter(LocalDateTime.now()))
                    .min(Comparator.comparing(Booking::getStart))
                    .ifPresent(booking -> itemWithDatesDto.setNextBooking(booking.getStart()));
        }
        return items;
    }

    @Override
    public ItemWithDatesDto findById(long itemId) {
        final Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id - " + itemId + " не найдена."));
        final ItemWithDatesDto itemWithDatesDto = ItemMapper.mapToItemWithDatesDto(item);
        itemWithDatesDto.setComments(
                commentRepository.findByItemIdOrderByCreatedDesc(itemId)
                        .stream()
                        .map(CommentMapper::mapToCommentDto)
                        .toList());
        return itemWithDatesDto;
    }

    @Override
    public ItemDto create(long userId, NewItemRequest newItem) {
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id - " + userId + " не найден."));
        final Item item = ItemMapper.mapToItem(newItem);
        item.setOwner(user);
        final Item itemWithId = itemRepository.save(item);
        return ItemMapper.mapToItemDto(itemWithId);
    }

    @Override
    public ItemDto update(long userId, long itemId, UpdateItemRequest updItem) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id - " + userId + " не найден."));
        final Item oldItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id - " + itemId + " не найдена."));
        if (oldItem.getOwner().getId() != userId)
            throw new NotFoundException("Вещь id - " + itemId + " пользователя id - " + userId + " не найдена.");
        final Item itemAfterUpdate = ItemMapper.updateItemFields(oldItem, updItem);
        return ItemMapper.mapToItemDto(itemRepository.save(itemAfterUpdate));
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text == null || text.isBlank()) return Collections.emptyList();
        return itemRepository.searchItems(text)
                .stream()
                .map(ItemMapper::mapToItemDto)
                .toList();
    }

    @Override
    public CommentDto createComment(long userId, long itemId, NewCommentRequest newComment) {
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id - " + userId + " не найден."));
        final Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id - " + itemId + " не найдена."));
        List<Booking> booking = bookingRepository
                .findByBookerIdAndEndBefore(userId, LocalDateTime.now(), Sort.by(Sort.Direction.DESC, "start"));
        if (booking.isEmpty())
            throw new NotAvailableException("userId", "Пользователь с id - " + userId + " не брал в аренду вещь с id - " + itemId);
        final Comment comment = CommentMapper.mapToComment(newComment, user, item);
        return CommentMapper.mapToCommentDto(commentRepository.save(comment));
    }
}
