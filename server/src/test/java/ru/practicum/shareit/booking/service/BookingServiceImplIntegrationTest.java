package ru.practicum.shareit.booking.service;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dal.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBooking;
import ru.practicum.shareit.booking.model.BookingRequestState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exceptions.NotAvailableException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.TimeValidationException;
import ru.practicum.shareit.item.dal.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItem;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.dal.UserRepository;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class BookingServiceImplIntegrationTest {
    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private BookingServiceImpl bookingService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemServiceImpl itemService;

    private UserDto user;
    private long userId;

    private ItemDto item;
    private long itemId;

    private NewBooking newBooking;
    private LocalDateTime now = LocalDateTime.now();
    BookingDto savedBooking;

    @BeforeEach
    void setUp() {
        NewUserRequest newUser = new NewUserRequest();
        newUser.setName("name");
        newUser.setEmail("email@email.com");
        user = userService.create(newUser);
        userId = user.getId();

        NewItem newItem = new NewItem();
        newItem.setName("name");
        newItem.setDescription("desc");
        newItem.setAvailable(true);
        item = itemService.create(userId, newItem);
        itemId = item.getId();

        newBooking = new NewBooking();
        now = LocalDateTime.now();
        newBooking.setStart(now.plusDays(1));
        newBooking.setEnd(now.plusDays(2));
        newBooking.setItemId(itemId);

        savedBooking = bookingService.create(userId, newBooking);
    }

    @AfterEach
    void tearDown() {
        bookingRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void create_shouldReturnCreatedBooking() {
        assertTrue(savedBooking.getId() > 0);
        assertThat(newBooking.getStart()).isEqualTo(savedBooking.getStart());
        assertThat(newBooking.getEnd()).isEqualTo(savedBooking.getEnd());
        assertThat(item).isEqualTo(savedBooking.getItem());
        assertThat(user).isEqualTo(savedBooking.getBooker());
        assertThat(BookingStatus.WAITING).isEqualTo(savedBooking.getStatus());
    }

    @Test
    void create_shouldThrowExceptions() {
        NewBooking newBooking2 = new NewBooking();
        now = LocalDateTime.now();
        newBooking2.setStart(now.plusDays(1));
        newBooking2.setEnd(now.plusDays(2));
        newBooking2.setItemId(itemId);

        //будет выброшено NotFoundException, т.к. пользователь не найден
        assertThrows(NotFoundException.class, () -> bookingService.create(100000, newBooking2));

        //будет выброшено NotFoundException, т.к. вещь не найдена
        newBooking2.setItemId(1000000L);
        assertThrows(NotFoundException.class, () -> bookingService.create(userId, newBooking2));
        newBooking2.setItemId(itemId);

        //будет выброшено TimeValidationException, т.к. дата end должна быть позднее даты start
        newBooking2.setEnd(now);
        assertThrows(TimeValidationException.class, () -> bookingService.create(userId, newBooking2));
        newBooking2.setEnd(now.plusDays(2));

        //будет выброшено NotAvailableException, т.к. вещь не доступна для бронирования
        UpdateItemRequest updateItemRequest = new UpdateItemRequest();
        updateItemRequest.setAvailable(false);
        itemService.update(userId, itemId, updateItemRequest);
        assertThrows(NotAvailableException.class, () -> bookingService.create(userId, newBooking2));
        UpdateItemRequest updateItemRequest2 = new UpdateItemRequest();
        updateItemRequest2.setAvailable(true);
        itemService.update(userId, itemId, updateItemRequest2);

        //будет выброшено TimeValidationException, т.к. вещь в эти даты уже забронирована
        NewBooking newBooking3 = new NewBooking();
        newBooking3.setStart(now.plusDays(1).minusHours(1));
        newBooking3.setEnd(now.plusDays(2).plusHours(1));
        newBooking3.setItemId(itemId);
        long bookingId = bookingService.create(userId, newBooking2).getId();
        bookingService.update(userId, bookingId, true);
        assertThrows(TimeValidationException.class, () -> bookingService.create(userId, newBooking3));
    }

    @Test
    void update_shouldReturnUpdatedBooking() {
        BookingDto updatedBooking = bookingService.update(userId, savedBooking.getId(), true);

        assertThat(BookingStatus.APPROVED).isEqualTo(updatedBooking.getStatus());
        assertThat(savedBooking).usingRecursiveComparison().ignoringFields("status").isEqualTo(savedBooking);

        updatedBooking = bookingService.update(userId, savedBooking.getId(), false);
        assertThat(BookingStatus.REJECTED).isEqualTo(updatedBooking.getStatus());
        assertThat(savedBooking).usingRecursiveComparison().ignoringFields("status").isEqualTo(savedBooking);
    }

    @Test
    void update_shouldThrowExceptions() {
        //будет выброшено NotAvailableException, т.к. пользователь не найден
        assertThrows(NotAvailableException.class, () -> bookingService.update(100000, savedBooking.getId(), true));

        //будет выброшено NotFoundException, т.к. бронирование не найдено
        assertThrows(NotFoundException.class, () -> bookingService.update(userId, 10000000, true));

        //будет выброшено NotAvailableException, т.к. только владелец вещи может потверждать бронирование
        NewUserRequest newUser = new NewUserRequest();
        newUser.setName("name");
        newUser.setEmail("email2@email.com");
        UserDto otherUser = userService.create(newUser);
        assertThrows(NotAvailableException.class, () -> bookingService.update(otherUser.getId(), savedBooking.getId(), true));
    }

    @Test
    void findById_shouldReturnBooking() {
        BookingDto actualBooking = bookingService.findById(userId, savedBooking.getId());

        assertThat(savedBooking).usingRecursiveComparison().isEqualTo(actualBooking);
    }

    @Test
    void findById_shouldThrowExceptions() {
        //будет выброшено NotFoundException, т.к. пользователь не найден
        assertThrows(NotFoundException.class, () -> bookingService.findById(100000, savedBooking.getId()));

        //будет выброшено NotFoundException, т.к. бронирование не найдено
        assertThrows(NotFoundException.class, () -> bookingService.findById(userId, 10000000));

        //будет выброшено NotAvailableException, т.к. только арендатор может получать информацию о бронировании
        NewUserRequest newUser = new NewUserRequest();
        newUser.setName("name");
        newUser.setEmail("email2@email.com");
        UserDto otherUser = userService.create(newUser);
        assertThrows(NotAvailableException.class, () -> bookingService.findById(otherUser.getId(), savedBooking.getId()));
    }

    @Test
    void findByBookerId_shouldReturnBookingsList() {
        bookingRepository.deleteAll();

        //проверка всех бронирований BookingRequestState.ALL
        List<BookingDto> bookings = bookingService.findByBookerId(userId, BookingRequestState.ALL).stream().toList();
        assertTrue(bookings.isEmpty());

        NewBooking newBooking1 = new NewBooking();
        newBooking1.setStart(now.minusDays(2));
        newBooking1.setEnd(now.plusDays(2));
        newBooking1.setItemId(itemId);
        BookingDto savedNewBooking1 = bookingService.create(userId, newBooking1);

        bookings = bookingService.findByBookerId(userId, BookingRequestState.ALL).stream().toList();
        BookingDto actualBooking = bookings.getFirst();
        assertThat(savedNewBooking1).usingRecursiveComparison().isEqualTo(actualBooking);
        bookingRepository.deleteAll();

        //проверка ожидающих подтверждение бронирований BookingRequestState.WAITING
        bookings = bookingService.findByBookerId(userId, BookingRequestState.WAITING).stream().toList();
        assertTrue(bookings.isEmpty());

        NewBooking newBooking2 = new NewBooking();
        newBooking2.setStart(now.minusDays(2));
        newBooking2.setEnd(now.plusDays(2));
        newBooking2.setItemId(itemId);
        BookingDto savedWaitingBooking = bookingService.create(userId, newBooking1);

        bookings = bookingService.findByBookerId(userId, BookingRequestState.WAITING).stream().toList();
        actualBooking = bookings.getFirst();
        assertThat(savedWaitingBooking).usingRecursiveComparison().isEqualTo(actualBooking);
        bookingRepository.deleteAll();

        //проверка текущих бронирований BookingRequestState.CURRENT
        bookings = bookingService.findByBookerId(userId, BookingRequestState.CURRENT).stream().toList();
        assertTrue(bookings.isEmpty());

        NewBooking newBooking3 = new NewBooking();
        newBooking3.setStart(now.minusDays(2));
        newBooking3.setEnd(now.plusDays(2));
        newBooking3.setItemId(itemId);
        BookingDto savedCurrentBookings = bookingService.create(userId, newBooking3);
        bookings = bookingService.findByBookerId(userId, BookingRequestState.CURRENT).stream().toList();
        BookingDto actualCurrentBookings = bookings.getFirst();
        assertThat(savedCurrentBookings).usingRecursiveComparison().isEqualTo(actualCurrentBookings);
        bookingRepository.deleteAll();

        //проверка прошедших бронирований BookingRequestState.PAST
        bookings = bookingService.findByBookerId(userId, BookingRequestState.PAST).stream().toList();
        assertTrue(bookings.isEmpty());

        NewBooking newBooking4 = new NewBooking();
        newBooking4.setStart(now.minusDays(2));
        newBooking4.setEnd(now.minusDays(1));
        newBooking4.setItemId(itemId);
        BookingDto savedPastBookings = bookingService.create(userId, newBooking4);
        bookings = bookingService.findByBookerId(userId, BookingRequestState.PAST).stream().toList();
        BookingDto actualPastBookings = bookings.getFirst();
        assertThat(savedPastBookings).usingRecursiveComparison().isEqualTo(actualPastBookings);
        bookingRepository.deleteAll();

        //проверка будущих бронирований BookingRequestState.FUTURE
        bookings = bookingService.findByBookerId(userId, BookingRequestState.FUTURE).stream().toList();
        assertTrue(bookings.isEmpty());

        NewBooking newBooking5 = new NewBooking();
        newBooking5.setStart(now.plusDays(1));
        newBooking5.setEnd(now.plusDays(2));
        newBooking5.setItemId(itemId);
        BookingDto savedFutureBookings = bookingService.create(userId, newBooking5);
        bookings = bookingService.findByBookerId(userId, BookingRequestState.FUTURE).stream().toList();
        BookingDto actualFutureBookings = bookings.getFirst();
        assertThat(savedFutureBookings).usingRecursiveComparison().isEqualTo(actualFutureBookings);
        bookingRepository.deleteAll();

        //проверка отклоненных бронирований BookingRequestState.REJECTED
        bookings = bookingService.findByBookerId(userId, BookingRequestState.REJECTED).stream().toList();
        assertTrue(bookings.isEmpty());

        NewBooking newBooking6 = new NewBooking();
        newBooking6.setStart(now.minusDays(2));
        newBooking6.setEnd(now.minusDays(1));
        newBooking6.setItemId(itemId);
        BookingDto savedRejectedBookings = bookingService.create(userId, newBooking6);
        bookingService.update(userId, savedRejectedBookings.getId(), false);
        bookings = bookingService.findByBookerId(userId, BookingRequestState.REJECTED).stream().toList();
        BookingDto actualRejectedBookings = bookings.getFirst();
        assertEquals(actualRejectedBookings.getStatus(), BookingStatus.REJECTED);
        assertThat(savedRejectedBookings).usingRecursiveComparison().ignoringFields("status").isEqualTo(actualRejectedBookings);
    }

    @Test
    void findByBookerId_shouldThrowNotFoundException() {
        assertThrows(NotFoundException.class, () -> bookingService.findByBookerId(1000000, BookingRequestState.REJECTED));
    }

    @Test
    void findByOwnerId_shouldReturnBookingsList() {
        bookingRepository.deleteAll();

        //проверка всех бронирований BookingRequestState.ALL
        List<BookingDto> bookings = bookingService.findByOwnerId(userId, BookingRequestState.ALL).stream().toList();
        assertTrue(bookings.isEmpty());

        NewBooking newBooking1 = new NewBooking();
        newBooking1.setStart(now.minusDays(2));
        newBooking1.setEnd(now.plusDays(2));
        newBooking1.setItemId(itemId);
        BookingDto savedNewBooking1 = bookingService.create(userId, newBooking1);

        bookings = bookingService.findByOwnerId(userId, BookingRequestState.ALL).stream().toList();
        BookingDto actualBooking = bookings.getFirst();
        assertThat(savedNewBooking1).usingRecursiveComparison().isEqualTo(actualBooking);
        bookingRepository.deleteAll();

        //проверка ожидающих подтверждение бронирований BookingRequestState.WAITING
        bookings = bookingService.findByOwnerId(userId, BookingRequestState.WAITING).stream().toList();
        assertTrue(bookings.isEmpty());

        NewBooking newBooking2 = new NewBooking();
        newBooking2.setStart(now.minusDays(2));
        newBooking2.setEnd(now.plusDays(2));
        newBooking2.setItemId(itemId);
        BookingDto savedWaitingBooking = bookingService.create(userId, newBooking1);

        bookings = bookingService.findByOwnerId(userId, BookingRequestState.WAITING).stream().toList();
        actualBooking = bookings.getFirst();
        assertThat(savedWaitingBooking).usingRecursiveComparison().isEqualTo(actualBooking);
        bookingRepository.deleteAll();

        //проверка текущих бронирований BookingRequestState.CURRENT
        bookings = bookingService.findByOwnerId(userId, BookingRequestState.CURRENT).stream().toList();
        assertTrue(bookings.isEmpty());

        NewBooking newBooking3 = new NewBooking();
        newBooking3.setStart(now.minusDays(2));
        newBooking3.setEnd(now.plusDays(2));
        newBooking3.setItemId(itemId);
        BookingDto savedCurrentBookings = bookingService.create(userId, newBooking3);
        bookings = bookingService.findByOwnerId(userId, BookingRequestState.CURRENT).stream().toList();
        BookingDto actualCurrentBookings = bookings.getFirst();
        assertThat(savedCurrentBookings).usingRecursiveComparison().isEqualTo(actualCurrentBookings);
        bookingRepository.deleteAll();

        //проверка прошедших бронирований BookingRequestState.PAST
        bookings = bookingService.findByOwnerId(userId, BookingRequestState.PAST).stream().toList();
        assertTrue(bookings.isEmpty());

        NewBooking newBooking4 = new NewBooking();
        newBooking4.setStart(now.minusDays(2));
        newBooking4.setEnd(now.minusDays(1));
        newBooking4.setItemId(itemId);
        BookingDto savedPastBookings = bookingService.create(userId, newBooking4);
        bookings = bookingService.findByOwnerId(userId, BookingRequestState.PAST).stream().toList();
        BookingDto actualPastBookings = bookings.getFirst();
        assertThat(savedPastBookings).usingRecursiveComparison().isEqualTo(actualPastBookings);
        bookingRepository.deleteAll();

        //проверка будущих бронирований BookingRequestState.FUTURE
        bookings = bookingService.findByOwnerId(userId, BookingRequestState.FUTURE).stream().toList();
        assertTrue(bookings.isEmpty());

        NewBooking newBooking5 = new NewBooking();
        newBooking5.setStart(now.plusDays(1));
        newBooking5.setEnd(now.plusDays(2));
        newBooking5.setItemId(itemId);
        BookingDto savedFutureBookings = bookingService.create(userId, newBooking5);
        bookings = bookingService.findByOwnerId(userId, BookingRequestState.FUTURE).stream().toList();
        BookingDto actualFutureBookings = bookings.getFirst();
        assertThat(savedFutureBookings).usingRecursiveComparison().isEqualTo(actualFutureBookings);
        bookingRepository.deleteAll();

        //проверка отклоненных бронирований BookingRequestState.REJECTED
        bookings = bookingService.findByOwnerId(userId, BookingRequestState.REJECTED).stream().toList();
        assertTrue(bookings.isEmpty());

        NewBooking newBooking6 = new NewBooking();
        newBooking6.setStart(now.minusDays(2));
        newBooking6.setEnd(now.minusDays(1));
        newBooking6.setItemId(itemId);
        BookingDto savedRejectedBookings = bookingService.create(userId, newBooking6);
        bookingService.update(userId, savedRejectedBookings.getId(), false);
        bookings = bookingService.findByOwnerId(userId, BookingRequestState.REJECTED).stream().toList();
        BookingDto actualRejectedBookings = bookings.getFirst();
        assertEquals(actualRejectedBookings.getStatus(), BookingStatus.REJECTED);
        assertThat(savedRejectedBookings).usingRecursiveComparison().ignoringFields("status").isEqualTo(actualRejectedBookings);
    }

    @Test
    void findByOwnerId_shouldThrowNotFoundException() {
        assertThrows(NotFoundException.class, () -> bookingService.findByOwnerId(1000000, BookingRequestState.REJECTED));
    }
}