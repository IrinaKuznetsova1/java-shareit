package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dal.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.NewBooking;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingRequestState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exceptions.NotAvailableException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.TimeValidationException;
import ru.practicum.shareit.item.dal.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dal.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public BookingDto create(long userId, NewBooking newBooking) {
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id - " + userId + " не найден."));
        final Item item = itemRepository.findById(newBooking.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещь с id - " + newBooking.getItemId() + " не найдена."));
        if (newBooking.getStart().equals(newBooking.getEnd()) || newBooking.getStart().isAfter(newBooking.getEnd()))
            throw new TimeValidationException("end", "Дата окончания должна быть позднее даты начала.");
        if (!item.isAvailable())
            throw new NotAvailableException("itemId", "Вещь с id - " + item.getId() + " недоступна для бронирования.");
        if (bookingRepository.isTimeCrossing(item.getId(), newBooking.getStart(), newBooking.getEnd()))
            throw new TimeValidationException("start, end", "Вещь в указанные даты не доступна для бронирования.");
        final Booking booking = BookingMapper.mapToBooking(newBooking);
        booking.setItem(item);
        booking.setBooker(user);
        final Booking bookingWithId = bookingRepository.save(booking);
        return BookingMapper.mapToBookingDto(bookingWithId);
    }

    @Override
    public BookingDto update(long userId, long bookingId, boolean isApproved) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotAvailableException("userId", "Пользователь с id - " + userId + " не найден."));
        final Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с id - " + bookingId + " не найдено."));
        if (booking.getItem().getOwner().getId() != userId)
            throw new NotAvailableException("userId", "Управлять бронированием может только владелец вещи.");
        if (isApproved)
            booking.setStatus(BookingStatus.APPROVED);
        if (!isApproved)
            booking.setStatus(BookingStatus.REJECTED);
        bookingRepository.save(booking);
        return BookingMapper.mapToBookingDto(booking);
    }

    @Override
    public BookingDto findById(long userId, long bookingId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id - " + userId + " не найден."));
        final Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с id - " + bookingId + " не найдено."));
        if (booking.getBooker().getId() != userId && booking.getItem().getOwner().getId() != userId)
            throw new NotAvailableException("userId", "Получить информацию о бронировании может только владелец вещи или арендатор.");
        return BookingMapper.mapToBookingDto(booking);
    }

    @Override
    public Collection<BookingDto> findByBookerId(long userId, BookingRequestState state) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id - " + userId + " не найден."));
        LocalDateTime now = LocalDateTime.now();
        Sort newestFirst = Sort.by(Sort.Direction.DESC, "start");
        List<Booking> bookingList = switch (state) {
            case ALL -> bookingRepository.findByBookerId(userId, newestFirst);
            case CURRENT -> bookingRepository.findByBookerIdAndStartBeforeAndEndAfter(userId, now, now, newestFirst);
            case PAST -> bookingRepository.findByBookerIdAndEndBefore(userId, now, newestFirst);
            case FUTURE -> bookingRepository.findByBookerIdAndStartAfter(userId, now, newestFirst);
            case WAITING -> bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.WAITING, newestFirst);
            case REJECTED -> bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.REJECTED, newestFirst);
        };
        return bookingList.stream().map(BookingMapper::mapToBookingDto).toList();
    }

    @Override
    public Collection<BookingDto> findByOwnerId(long userId, BookingRequestState state) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id - " + userId + " не найден."));
        LocalDateTime now = LocalDateTime.now();
        Sort newestFirst = Sort.by(Sort.Direction.DESC, "start");
        List<Booking> bookingList = switch (state) {
            case ALL -> bookingRepository.findByItemOwnerId(userId, newestFirst);
            case CURRENT -> bookingRepository.findByItemOwnerIdAndStartBeforeAndEndAfter(userId, now, now, newestFirst);
            case PAST -> bookingRepository.findByItemOwnerIdAndEndBefore(userId, now, newestFirst);
            case FUTURE -> bookingRepository.findByItemOwnerIdAndStartAfter(userId, now, newestFirst);
            case WAITING -> bookingRepository.findByItemOwnerIdAndStatus(userId, BookingStatus.WAITING, newestFirst);
            case REJECTED -> bookingRepository.findByItemOwnerIdAndStatus(userId, BookingStatus.REJECTED, newestFirst);
        };
        return bookingList.stream().map(BookingMapper::mapToBookingDto).toList();
    }
}
