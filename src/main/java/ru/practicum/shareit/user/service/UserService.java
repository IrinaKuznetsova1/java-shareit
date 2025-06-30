package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> findAll();

    UserDto findById(long userId);

    UserDto create(NewUserRequest newUser);

    UserDto update(long id, UpdateUserRequest updUser);

    UserDto deleteUser(long userId);
}
