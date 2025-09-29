package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NewItemRequest {
    @NotBlank(message = "описание не должно быть null или быть пустым")
    @Size(max = 512, message = "максимальная длина описания - 512 символов")
    private String description;
}
