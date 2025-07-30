package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NewItemRequest {
    @NotBlank(message = "имя не должно быть null или быть пустым")
    @Size(max = 256, message = "максимальная длина названия - 256 символов")
    private String name;

    @NotBlank(message = "описание не должно быть null или быть пустым")
    @Size(max = 512, message = "максимальная длина описания - 512 символов")
    private String description;

    @NotNull(message = "статус не должен быть null")
    private Boolean available;
}
