package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {
    ItemDto create(ItemDto itemDto, Long userId);

    ItemDto get(Long id);

    Collection<ItemDto> getAll(Long userId);

    ItemDto update(ItemDto itemDto, Long userId);

    Collection<ItemDto> searchAvailableItems(String text);
}
