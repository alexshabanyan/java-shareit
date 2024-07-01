package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.args.CreateCommentArgs;
import ru.practicum.shareit.item.args.CreateItemArgs;
import ru.practicum.shareit.item.args.UpdateItemArgs;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ItemService {
    Item create(CreateItemArgs createItemArgs);

    Item get(Long id, Long userId);

    List<Item> getAll(Long userId, int from, int size);

    Item update(UpdateItemArgs updateItemArgs, Long itemId, Long userId);

    List<Item> searchAvailableItems(String text, int from, int size);

    Comment createComment(CreateCommentArgs createCommentArgs);

    Map<Long, List<Comment>> getItemCommentMapping(Set<Long> itemIds);

    Map<Long, List<Item>> getRequestItemMapping(Set<Long> requestIds);
}
