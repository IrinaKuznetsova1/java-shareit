package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<UserDto> findAll() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public UserDto findUserById(@PathVariable @Min(1) long id) {
        return userService.findById(id);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public UserDto create(@Valid @RequestBody NewUserRequest newUser) {
        return userService.create(newUser);
    }

    @PatchMapping("/{id}")
    public UserDto update(@PathVariable @Min(1) long id, @Valid @RequestBody UpdateUserRequest updUser) {
        return userService.update(id, updUser);
    }

    @DeleteMapping("/{id}")
    public UserDto deleteUser(@PathVariable @Min(1) long id) {
        return userService.deleteUser(id);
    }
}
