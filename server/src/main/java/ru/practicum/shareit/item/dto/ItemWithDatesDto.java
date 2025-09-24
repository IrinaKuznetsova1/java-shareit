package ru.practicum.shareit.item.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemWithDatesDto {
    private long id;
    private String name;
    private String description;
    private boolean available;
    private LocalDateTime lastBooking;
    private LocalDateTime nextBooking;
    private List<CommentDto> comments = new ArrayList<>();
}
