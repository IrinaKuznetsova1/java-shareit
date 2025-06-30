package ru.practicum.shareit.user.dal;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private long counter = 0;

    @Override
    public List<User> getAll() {
        return users.values().stream().toList();
    }

    @Override
    public Optional<User> getById(long userId) {
        return Optional.ofNullable(users.get(userId));
    }

    @Override
    public User save(User user) {
        user.setId(++counter);
        users.put(counter, user);
        return user;
    }

    @Override
    public User saveUpdatedObject(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> remove(long userId) {
        return Optional.ofNullable(users.remove(userId));
    }

    @Override
    public boolean emailIsDuplicated(String email) {
        return getAll()
                .stream()
                .map(User::getEmail)
                .anyMatch(str -> str.equals(email));
    }
}
