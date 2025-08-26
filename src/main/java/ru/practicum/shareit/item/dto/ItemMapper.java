package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Item;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {
    public static ItemDto mapToItemDto(Item item) {
        return new ItemDto(item.getId(), item.getName(), item.getDescription(), item.isAvailable());
    }

    public static ItemWithDatesDto mapToItemWithDatesDto(Item item) {
        ItemWithDatesDto itemWithDatesDto = new ItemWithDatesDto();
        itemWithDatesDto.setId(item.getId());
        itemWithDatesDto.setName(item.getName());
        itemWithDatesDto.setDescription(item.getDescription());
        itemWithDatesDto.setAvailable(item.isAvailable());
        return itemWithDatesDto;
    }

    public static Item mapToItem(NewItemRequest request) {
        Item item = new Item();
        item.setName(request.getName());
        item.setDescription(request.getDescription());
        item.setAvailable(request.getAvailable());
        return item;
    }

    public static Item updateItemFields(Item item, UpdateItemRequest request) {
        if (request.hasName())
            item.setName(request.getName());
        if (request.hasDescription())
            item.setDescription(request.getDescription());
        if (request.hasAvailable())
            item.setAvailable(request.getAvailable());
        return item;
    }

}
