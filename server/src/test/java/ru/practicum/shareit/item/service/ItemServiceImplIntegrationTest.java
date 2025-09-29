package ru.practicum.shareit.item.service;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dal.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBooking;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exceptions.NotAvailableException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dal.ItemRepository;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.request.dal.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.dal.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ItemServiceImplIntegrationTest {
    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemServiceImpl itemService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private BookingServiceImpl bookingService;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private ItemRequestServiceImpl itemRequestService;

    private final User user = new User(1, "email@email.com", "name");
    private long userId;
    private NewItem newItem;

    @BeforeEach
    void setup() {
        userId = userRepository.save(user).getId();
        newItem = new NewItem();
        newItem.setName("name");
        newItem.setDescription("description");
        newItem.setAvailable(true);
    }

    @AfterEach
    void clear() {
        itemRequestRepository.deleteAll();
        bookingRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void create_shouldReturnCreatedItem() {
        //создаем и сохраняем user2
        User user2 = userRepository.save(new User(2, "email2@email.com", "name2"));

        //создаем и сохраняем itemRequest
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription("description");
        itemRequest.setRequestor(user2);
        ItemRequest savedItemRequest = itemRequestRepository.save(itemRequest);

        //добавляем запрос в новый item
        newItem.setRequestId(savedItemRequest.getId());

        ItemDto savedItem = itemService.create(userId, newItem);

        //проверяем корректность сохранения item
        assertTrue(savedItem.getId() > 0);
        assertThat(newItem.getName()).isEqualTo(savedItem.getName());
        assertThat(newItem.getDescription()).isEqualTo(savedItem.getDescription());
        assertThat(newItem.getAvailable()).isEqualTo(savedItem.isAvailable());

        //проверяем, что вещь, созданная по запросу, отображается в "Set<> items" itemRequest
        ItemRequestDto requestWithItem = itemRequestService.findById(savedItemRequest.getId());
        Set<ItemDto> items = requestWithItem.getItems();
        assertTrue(items.contains(savedItem));
    }

    @Test
    void create_shouldThrowNotFoundException() {
        //указан несуществующий пользователь
        assertThrows(NotFoundException.class, () -> itemService.create(100000, newItem));

        //указан несуществующий requestId
        newItem.setRequestId(100000000L);
        assertThrows(NotFoundException.class, () -> itemService.create(userId, newItem));
    }

    @Test
    void findItemsByOwnerId_shouldReturnItemsList() {
        ItemDto savedItem = itemService.create(userId, newItem);

        //new User
        User otherUser = new User(2, "email2@email.com", "name2");
        long otherUserId = userRepository.save(otherUser).getId();

        //new Bookings in the past
        NewBooking newBooking = new NewBooking();
        LocalDateTime now = LocalDateTime.now();
        newBooking.setStart(now.minusDays(2));
        newBooking.setEnd(now.minusDays(1));
        newBooking.setItemId(savedItem.getId());
        BookingDto pastBooking = bookingService.create(otherUserId, newBooking);

        //new Bookings in the future
        NewBooking newBooking2 = new NewBooking();
        newBooking2.setStart(now.plusDays(1));
        newBooking2.setEnd(now.plusDays(2));
        newBooking2.setItemId(savedItem.getId());
        BookingDto futureBooking = bookingService.create(otherUserId, newBooking2);

        //new Comment
        NewCommentRequest newComment = new NewCommentRequest();
        newComment.setText("text");
        CommentDto savedComment = itemService.createComment(otherUserId, savedItem.getId(), newComment);

        List<ItemWithDatesDto> items = itemService.findItemsByOwnerId(userId);
        assertEquals(items.size(), 1);

        ItemWithDatesDto itemWithDatesDto = items.getFirst();

        assertThat(savedItem).usingRecursiveComparison().isEqualTo(itemWithDatesDto); //проверка полей id, name, description, available
        assertThat(pastBooking.getStart()).isEqualTo(itemWithDatesDto.getLastBooking()); //проверка lastBooking
        assertThat(futureBooking.getStart()).isEqualTo(itemWithDatesDto.getNextBooking()); //проверка nextBooking
        assertThat(itemWithDatesDto.getComments().size()).isEqualTo(1); //проверка List comments
        assertThat(itemWithDatesDto.getComments().getFirst()).usingRecursiveComparison().isEqualTo(savedComment); //проверка comment
    }

    @Test
    void findItemsByOwnerId_shouldThrowNotFoundException() {
        assertThrows(NotFoundException.class, () -> itemService.findItemsByOwnerId(100000));
    }

    @Test
    void findById_shouldReturnItem() {
        //сохраняем newItem
        ItemDto savedItem = itemService.create(userId, newItem);

        //new User
        User otherUser = new User(2, "email2@email.com", "name2");
        long otherUserId = userRepository.save(otherUser).getId();

        //new Bookings in the past
        NewBooking newBooking = new NewBooking();
        LocalDateTime now = LocalDateTime.now();
        newBooking.setStart(now.minusDays(2));
        newBooking.setEnd(now.minusDays(1));
        newBooking.setItemId(savedItem.getId());
        bookingService.create(otherUserId, newBooking);

        //new Comment
        NewCommentRequest newComment = new NewCommentRequest();
        newComment.setText("text");
        CommentDto savedComment = itemService.createComment(otherUserId, savedItem.getId(), newComment);

        ItemWithDatesDto itemWithDatesDto = itemService.findById(savedItem.getId());

        assertThat(savedItem).usingRecursiveComparison().isEqualTo(itemWithDatesDto);
        assertThat(itemWithDatesDto.getComments().getFirst()).usingRecursiveComparison().isEqualTo(savedComment);
    }

    @Test
    void findById_shouldThrowNotFoundException() {
        assertThrows(NotFoundException.class, () -> itemService.findById(100000));
    }


    @Test
    void update_shouldReturnUpdatedItem() {
        ItemDto savedItem = itemService.create(userId, newItem);

        //создаем запрос на обновление savedItem
        UpdateItemRequest updateItemRequest = new UpdateItemRequest();
        updateItemRequest.setName("upd name");
        updateItemRequest.setDescription("upd description");
        updateItemRequest.setAvailable(false);

        ItemDto updatedItem = itemService.update(userId, savedItem.getId(), updateItemRequest);

        assertThat(savedItem.getId()).isEqualTo(updatedItem.getId());
        assertThat(updateItemRequest).usingRecursiveComparison().ignoringFields("id")
                .isEqualTo(updatedItem);
    }

    @Test
    void update_shouldThrowNotFoundException() {
        ItemDto savedItem = itemService.create(userId, newItem);

        //создаем запрос на обновление savedItem
        UpdateItemRequest updateItemRequest = new UpdateItemRequest();
        updateItemRequest.setName("upd name");
        updateItemRequest.setDescription("upd description");
        updateItemRequest.setAvailable(false);

        //указан несуществующий пользователь
        assertThrows(NotFoundException.class, () -> itemService.update(100000, savedItem.getId(), updateItemRequest));

        //указан несуществующий itemId
        assertThrows(NotFoundException.class, () -> itemService.update(userId, 10000000, updateItemRequest));

        //пользователь не является владельцем  вещи
        final User otherUser = new User(2, "email2@email.com", "name2");
        long otherUserId = userRepository.save(otherUser).getId();

        assertThrows(NotFoundException.class, () -> itemService.update(otherUserId, savedItem.getId(), updateItemRequest));
    }

    @Test
    void searchItems() {
        ItemDto savedItem = itemService.create(userId, newItem);

        //поиск по имени
        List<ItemDto> items = itemService.searchItems("am");
        assertThat(savedItem).usingRecursiveComparison().isEqualTo(items.getFirst());

        //поиск по описанию
        items = itemService.searchItems("escr");
        assertThat(savedItem).usingRecursiveComparison().isEqualTo(items.getFirst());

        //вещь не найдена
        items = itemService.searchItems("name name");
        assertTrue(items.isEmpty());

        //пустой запрос
        items = itemService.searchItems("");
        assertTrue(items.isEmpty());
    }

    @Test
    void createComment() {
        ItemDto savedItem = itemService.create(userId, newItem);

        //new User
        User otherUser = new User(2, "email2@email.com", "name2");
        long otherUserId = userRepository.save(otherUser).getId();

        //new Booking
        NewBooking newBooking = new NewBooking();
        LocalDateTime now = LocalDateTime.now();
        newBooking.setStart(now.minusDays(2));
        newBooking.setEnd(now.minusDays(1));
        newBooking.setItemId(savedItem.getId());
        bookingService.create(otherUserId, newBooking);

        //new Comment
        NewCommentRequest newComment = new NewCommentRequest();
        newComment.setText("text");

        CommentDto savedComment = itemService.createComment(otherUserId, savedItem.getId(), newComment);

        //проверка savedComment
        assertTrue(savedComment.getId() > 0);
        assertThat(newComment.getText()).isEqualTo(savedComment.getText());
        assertThat(otherUser.getName()).isEqualTo(savedComment.getAuthorName());
        assertNotNull(savedComment.getCreated());

        //проверка List comments savedItem
        ItemWithDatesDto itemWithDatesDto = itemService.findById(savedItem.getId());
        assertThat(itemWithDatesDto.getComments().getFirst()).usingRecursiveComparison().isEqualTo(savedComment);

        //проверка исключения NotFoundException при несуществующем пользователе
        assertThrows(NotFoundException.class,
                () -> itemService.createComment(10000000, savedItem.getId(), newComment));

        //проверка исключения NotFoundException при несуществующем вещи
        assertThrows(NotFoundException.class,
                () -> itemService.createComment(otherUserId, 1000000, newComment));

        //проверка исключения NotAvailableException при отсутствии бронирования вещи
        bookingRepository.deleteAll();
        assertThrows(NotAvailableException.class,
                () -> itemService.createComment(otherUserId, savedItem.getId(), newComment));

        //проверка исключения NotAvailableException при незавершенном бронировании
        NewBooking newBooking2 = new NewBooking();
        newBooking2.setStart(now.minusDays(2));
        newBooking2.setEnd(now.plusDays(2));
        newBooking2.setItemId(savedItem.getId());
        bookingService.create(otherUserId, newBooking2);
        assertThrows(NotAvailableException.class,
                () -> itemService.createComment(otherUserId, savedItem.getId(), newComment));
    }
}