package ru.practicum.shareit.item.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.model.Item;

@UtilityClass
public class ItemMapper {
    public Item toModel(ItemDto itemDto, Long userId) {
        if (itemDto == null || userId == null) {
            return null;
        }
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .ownerId(userId)
                .build();
    }

    public ItemDto toDto(Item item) {
        if (item == null) {
            return null;
        }
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    public Item updateModel(Item oldItem, Item newItem) {
        if (oldItem == null || newItem == null) {
            return null;
        }
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
