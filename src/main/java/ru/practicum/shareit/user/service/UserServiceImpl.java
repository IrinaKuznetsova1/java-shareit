package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.DuplicatedDataException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dal.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::mapToUserDto)
                .toList();
    }

    @Override
    public UserDto findById(long userId) {
        return userRepository.findById(userId)
                .map(UserMapper::mapToUserDto)
                .orElseThrow(() -> new NotFoundException("Пользователь с id - " + userId + " не найден."));
    }

    @Override
    public UserDto create(NewUserRequest newUser) {
        if (userRepository.findByEmail(newUser.getEmail()).isPresent())
            throw new DuplicatedDataException("email", "Пользователь с указанным email уже существует.");
        final User user = userRepository.save(UserMapper.mapToUser(newUser));
        return UserMapper.mapToUserDto(user);
    }

    @Override
    public UserDto update(long id, UpdateUserRequest updUser) {
        final User oldUser = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id - " + id + " не найден."));
        if (userRepository.findFirstByEmailAndIdNot(updUser.getEmail(), id).isPresent())
            throw new DuplicatedDataException("email", "Пользователь с указанным email уже существует.");
        final User userAfterUpdate = UserMapper.updateUserFields(oldUser, updUser);
        return UserMapper.mapToUserDto(userRepository.save(userAfterUpdate));
    }

    @Override
    public void deleteUser(long userId) {
        userRepository.deleteById(userId);
    }
}
