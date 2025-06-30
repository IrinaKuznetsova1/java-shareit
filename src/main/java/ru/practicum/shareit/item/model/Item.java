package ru.practicum.shareit.item.model;

import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;

@Data
public class Item {
    private long id;
    private String name;
    private String description;
    private boolean available;
    private long owner;
    private ItemRequest itemRequest;
}
