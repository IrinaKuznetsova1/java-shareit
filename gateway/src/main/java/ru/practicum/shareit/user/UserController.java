package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> findAll() {
        return userClient.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findUserById(@PathVariable @Min(1) long id) {
        return userClient.findById(id);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody NewUserRequest newUser) {
        return userClient.create(newUser);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable @Min(1) long id,
                                         @Valid @RequestBody UpdateUserRequest updUser) {
        return userClient.update(id, updUser);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable @Min(1) long id) {
        userClient.deleteUser(id);
    }
}
