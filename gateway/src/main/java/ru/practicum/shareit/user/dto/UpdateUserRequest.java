package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateUserRequest {
    @Size(max = 256, message = "максимальная длина имени - 256 символов")
    private String name;

    @Email(message = "строка должна соответствовать формату адреса электронной почты")
    @Size(max = 256, message = "максимальная длина email - 256 символов")
    private String email;

    public boolean hasName() {
        return !(name == null || name.isBlank());
    }

    public boolean hasEmail() {
        return !(email == null || email.isBlank());
    }
}
