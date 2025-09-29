package ru.practicum.shareit.request.service;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.request.dal.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequest;
import ru.practicum.shareit.user.dal.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ItemRequestServiceImplIntegrationTest {
    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private ItemRequestServiceImpl itemRequestService;

    @Autowired
    private UserRepository userRepository;

    private final User user = new User(1, "email@email.com", "name");
    private long userId;

    @BeforeEach
    void setup() {
        userId = userRepository.save(user).getId();
    }

    @AfterEach
    void clear() {
        itemRequestRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void create_shouldReturnCreatedItemRequestDto() {
        NewItemRequest newItemRequest = new NewItemRequest();
        newItemRequest.setDescription("description");
        ItemRequestDto savedItemRequest = itemRequestService.create(userId, newItemRequest);

        assertTrue(savedItemRequest.getId() > 0);
        assertThat(newItemRequest.getDescription()).isEqualTo(savedItemRequest.getDescription());
        assertNotNull(savedItemRequest.getCreated());
        assertThat(userId).isEqualTo(savedItemRequest.getRequestor().getId());
    }

    @Test
    void create_shouldThrowNotFoundException() {
        NewItemRequest newItemRequest = new NewItemRequest();
        newItemRequest.setDescription("description");

        assertThrows(NotFoundException.class, () -> itemRequestService.create(100000, newItemRequest));
    }

    @Test
    void findItemRequestsByUserId_shouldReturnRequestsList() {
        NewItemRequest newItemRequest = new NewItemRequest();
        newItemRequest.setDescription("description");
        ItemRequestDto savedItemRequest = itemRequestService.create(userId, newItemRequest);

        List<ItemRequestDto> requestDtos = itemRequestService.findItemRequestsByUserId(userId);
        assertThat(requestDtos.size()).isEqualTo(1);

        ItemRequestDto actualItemRequest = requestDtos.getFirst();
        assertThat(actualItemRequest)
                .usingRecursiveComparison()
                .ignoringFields("items")
                .isEqualTo(savedItemRequest);

        //проверка сортировки по дате (от более новым к старым)
        NewItemRequest newItemRequest2 = new NewItemRequest();
        newItemRequest2.setDescription("description2");
        ItemRequestDto savedItemRequest2 = itemRequestService.create(userId, newItemRequest2);

        List<ItemRequestDto> requestDtos2 = itemRequestService.findItemRequestsByUserId(userId);
        assertThat(requestDtos2.size()).isEqualTo(2);
        assertThat(requestDtos2.getFirst())
                .usingRecursiveComparison()
                .ignoringFields("items")
                .isEqualTo(savedItemRequest2);
        assertThat(requestDtos2.getLast())
                .usingRecursiveComparison()
                .ignoringFields("items")
                .isEqualTo(savedItemRequest);
    }

    @Test
    void findItemRequestsByUserId_shouldThrowNotFoundException() {
        assertThrows(NotFoundException.class, () -> itemRequestService.findItemRequestsByUserId(100000));
    }

    @Test
    void findOtherUsersItemRequests_shouldReturnRequestsList() {
        NewItemRequest newItemRequest = new NewItemRequest();
        newItemRequest.setDescription("description");
        itemRequestService.create(userId, newItemRequest); //этот itemRequest не должен попасть в список

        User otherUser = new User(2, "email2@email.com", "name2");
        otherUser = userRepository.save(otherUser);
        NewItemRequest newItemRequest1 = new NewItemRequest();
        newItemRequest1.setDescription("description");
        ItemRequestDto savedItemRequest1 = itemRequestService.create(otherUser.getId(), newItemRequest1);

        NewItemRequest newItemRequest2 = new NewItemRequest();
        newItemRequest2.setDescription("description2");
        ItemRequestDto savedItemRequest2 = itemRequestService.create(otherUser.getId(), newItemRequest2);

        List<ItemRequestDto> requestDtos2 = itemRequestService.findOtherUsersItemRequests(userId);
        assertThat(requestDtos2.size()).isEqualTo(2);
        assertThat(requestDtos2.getFirst())
                .usingRecursiveComparison()
                .ignoringFields("items")
                .isEqualTo(savedItemRequest2);
        assertThat(requestDtos2.getLast())
                .usingRecursiveComparison()
                .ignoringFields("items")
                .isEqualTo(savedItemRequest1);
    }

    @Test
    void findOtherUsersItemRequests_shouldThrowNotFoundException() {
        assertThrows(NotFoundException.class, () -> itemRequestService.findOtherUsersItemRequests(100000));
    }

    @Test
    void findById_shouldReturnRequest() {
        NewItemRequest newItemRequest = new NewItemRequest();
        newItemRequest.setDescription("description");
        long requestId = itemRequestService.create(userId, newItemRequest).getId();

        ItemRequestDto savedRequest = itemRequestService.findById(requestId);
        assertEquals(savedRequest.getId(), requestId);
        assertThat(newItemRequest.getDescription()).isEqualTo(savedRequest.getDescription());
        assertNotNull(savedRequest.getCreated());
        assertThat(userId).isEqualTo(savedRequest.getRequestor().getId());
    }

    @Test
    void findById__shouldThrowNotFoundException() {
        assertThrows(NotFoundException.class, () -> itemRequestService.findById(100000));
    }
}