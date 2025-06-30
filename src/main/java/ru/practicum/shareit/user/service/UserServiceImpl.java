package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeptoins.DuplicatedDataException;
import ru.practicum.shareit.exeptoins.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dal.UserStorage;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Override
    public List<UserDto> findAll() {
        return userStorage.getAll()
                .stream()
                .map(UserMapper::mapToUserDto)
                .toList();
    }

    @Override
    public UserDto findById(long userId) {
        final User user = userStorage.getById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id - " + userId + " не найден."));
        return UserMapper.mapToUserDto(user);
    }

    @Override
    public UserDto create(NewUserRequest newUser) {
        if (userStorage.emailIsDuplicated(newUser.getEmail()))
            throw new DuplicatedDataException("email", "Указанный email уже используется.");
        final User user = userStorage.save(UserMapper.mapToUser(newUser));
        return UserMapper.mapToUserDto(user);
    }

    @Override
    public UserDto update(long id, UpdateUserRequest updUser) {
        final User oldUser = userStorage.getById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id - " + id + " не найден."));
        if (!oldUser.getEmail().equals(updUser.getEmail()) && userStorage.emailIsDuplicated(updUser.getEmail()))
            throw new DuplicatedDataException("email", "Указанный email уже используется.");
        final User userAfterUpdate = UserMapper.updateUserFields(oldUser, updUser);
        return UserMapper.mapToUserDto(userStorage.saveUpdatedObject(userAfterUpdate));
    }

    @Override
    public void deleteUser(long userId) {
        userStorage.remove(userId).orElseThrow(() -> new NotFoundException("Пользователь с id - " + userId + " не найден."));
    }
}
