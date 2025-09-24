package ru.practicum.shareit.user.service;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exceptions.DuplicatedDataException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dal.UserRepository;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UserServiceImplIntegrationTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserServiceImpl userService;

    @AfterEach
    void clear() {
        userRepository.deleteAll();
    }

    @Test
    void findAll_shouldReturnEmptyList() {
        assertThat(userService.findAll()).isEmpty();
    }

    @Test
    void findAll_shouldReturnUsersList() {
        NewUserRequest newUser = new NewUserRequest();
        newUser.setName("name");
        newUser.setEmail("email@email.com");
        userService.create(newUser);

        List<UserDto> users = userService.findAll();
        assertThat(users.size()).isEqualTo(1);
        UserDto savedUserDto = users.getFirst();
        assertTrue(savedUserDto.getId() > 0);
        assertThat(newUser).usingRecursiveComparison().ignoringFields("id").isEqualTo(savedUserDto);
    }

    @Test
    void findById_shouldReturnUser() {
        NewUserRequest newUser = new NewUserRequest();
        newUser.setName("name");
        newUser.setEmail("email@email.com");
        UserDto savedUser = userService.create(newUser);

        UserDto savedUserDto = userService.findById(savedUser.getId());
        assertEquals(savedUser.getId(), savedUserDto.getId());
        assertThat(newUser).usingRecursiveComparison().ignoringFields("id").isEqualTo(savedUserDto);
    }

    @Test
    void findById_shouldThrowNotFoundException() {
        assertThrows(NotFoundException.class, () -> userService.findById(1));
    }

    @Test
    void create_shouldReturnCreatedUser() {
        NewUserRequest newUser = new NewUserRequest();
        newUser.setName("name");
        newUser.setEmail("email@email.com");
        UserDto savedUserDto = userService.create(newUser);

        assertTrue(savedUserDto.getId() > 0);
        assertThat(newUser).usingRecursiveComparison().ignoringFields("id").isEqualTo(savedUserDto);
    }

    @Test
    void create_shouldThrowDuplicatedDataException() {
        NewUserRequest newUser = new NewUserRequest();
        newUser.setName("name");
        newUser.setEmail("email@email.com");
        userService.create(newUser);

        NewUserRequest newUserWithSameEmail = new NewUserRequest();
        newUserWithSameEmail.setEmail("email@email.com");

        assertThrows(DuplicatedDataException.class, () -> userService.create(newUserWithSameEmail));
    }

    @Test
    void update_shouldReturnUpdatedUser() {
        NewUserRequest newUser = new NewUserRequest();
        newUser.setName("name");
        newUser.setEmail("email@email.com");
        long id = userService.create(newUser).getId();

        UpdateUserRequest updateUserRequest = new UpdateUserRequest();
        updateUserRequest.setName("updated Name");
        updateUserRequest.setEmail("updated Email@email.com");
        UserDto updatedUserDto = userService.update(id, updateUserRequest);
        assertEquals(id, updatedUserDto.getId());
        assertThat(updateUserRequest).usingRecursiveComparison().ignoringFields("id").isEqualTo(updatedUserDto);
    }

    @Test
    void update_shouldThrowNotFoundException() {
        assertThrows(NotFoundException.class, () -> userService.update(1, new UpdateUserRequest()));
    }

    @Test
    void update_shouldThrowDuplicatedDataException() {
        NewUserRequest newUser1 = new NewUserRequest();
        newUser1.setName("name");
        newUser1.setEmail("email@email.com");
        UserDto user1 = userService.create(newUser1);

        NewUserRequest newUser2 = new NewUserRequest();
        newUser2.setName("name");
        newUser2.setEmail("email2@email.com");
        UserDto user2 = userService.create(newUser2);

        UpdateUserRequest updateUserRequest = new UpdateUserRequest();
        updateUserRequest.setName("updated Name");
        updateUserRequest.setEmail(user1.getEmail());

        assertThrows(DuplicatedDataException.class, () -> userService.update(user2.getId(), updateUserRequest));
    }

    @Test
    void deleteUser() {
        NewUserRequest newUser = new NewUserRequest();
        newUser.setName("name");
        newUser.setEmail("email@email.com");
        long id = userService.create(newUser).getId();
        userService.deleteUser(id);

        assertThrows(NotFoundException.class, () -> userService.findById(id));
    }
}