package ru.practicum.shareit.item.service;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

@UtilityClass
public class ItemMapper {
    public Item toModel(ItemDto itemDto, Long userId) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .ownerId(userId)
                .build();
    }

    public ItemDto toDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    public Item updateModel(Item oldItem, Item newItem) {
        Item.ItemBuilder builder = oldItem.toBuilder();
        if (newItem.getName() != null) {
            builder.name(newItem.getName());
        }
        if (newItem.getDescription() != null) {
            builder.description(newItem.getDescription());
        }
        if (newItem.getAvailable() != null) {
            builder.available(newItem.getAvailable());
        }
        return builder.build();
    }
}
