package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateItemRequest {
    @Size(max = 256, message = "максимальная длина названия - 256 символов")
    private String name;

    @Size(max = 512, message = "максимальная длина описания - 512 символов")
    private String description;
    private Boolean available;

    public boolean hasName() {
        return !(name == null || name.isBlank());
    }

    public boolean hasDescription() {
        return !(description == null || description.isBlank());
    }

    public boolean hasAvailable() {
        return !(available == null);
    }
}
