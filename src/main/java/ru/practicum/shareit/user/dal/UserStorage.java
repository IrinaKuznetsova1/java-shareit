package ru.practicum.shareit.user.dal;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    List<User> getAll();

    Optional<User> getById(long userId);

    User save(User user);

    User saveUpdatedObject(User user);

    Optional<User> remove(long userId);

    boolean emailIsDuplicated(String email);
}
