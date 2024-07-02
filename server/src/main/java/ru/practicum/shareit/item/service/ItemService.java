package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ItemService {
    Item create(ItemDto itemDto, Long userId);

    Item get(Long id, Long userId);

    List<Item> getAll(Long userId, int from, int size);

    Item update(ItemDto itemDto, Long itemId, Long userId);

    List<Item> searchAvailableItems(String text, int from, int size);

    Comment createComment(CommentDto commentDto, Long itemId, Long userId);

    Map<Long, List<Comment>> getItemCommentMapping(Set<Long> itemIds);

    Map<Long, List<Item>> getRequestItemMapping(Set<Long> requestIds);
}
