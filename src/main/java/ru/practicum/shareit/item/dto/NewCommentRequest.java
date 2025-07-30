package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NewCommentRequest {
    @NotBlank(message = "текст комментария не должен быть null или быть пустым")
    @Size(max = 512, message = "максимальная длина комментария - 512 символов")
    private String text;
}
