package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NewItemRequest {
    @NotBlank(message = "имя не должно быть null или быть пустым")
    private String name;

    @NotBlank(message = "описание не должно быть null или быть пустым")
    private String description;

    @NotNull(message = "статус не должен быть null")
    private Boolean available;
}
