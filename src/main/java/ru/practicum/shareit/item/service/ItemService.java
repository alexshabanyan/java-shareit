package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemExtendDto;

import java.util.Collection;

public interface ItemService {
    ItemDto create(ItemDto itemDto, Long userId);

    ItemExtendDto get(Long id, Long userId);

    Collection<ItemExtendDto> getAll(Long userId);

    ItemDto update(ItemDto itemDto, Long itemId, Long userId);

    Collection<ItemDto> searchAvailableItems(String text);

    CommentDto createComment(CommentDto commentDto, Long itemId, Long userId);
}
