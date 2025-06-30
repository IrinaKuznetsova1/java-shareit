package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NewUserRequest {
    @NotBlank(message = "имя не должно быть null или быть пустым")
    private String name;

    @NotBlank(message = "e-mail не должен быть null или быть пустым")
    @Email(message = "строка должна соответствовать формату адреса электронной почты")
    private String email;
}
