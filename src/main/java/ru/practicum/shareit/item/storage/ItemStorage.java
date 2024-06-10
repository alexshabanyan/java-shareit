package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface ItemStorage {
    Item create(Item item);

    Item update(Item item);

    Item get(Long id);

    Collection<Item> getAll(User user);

    Collection<Item> searchAvailableItems(String text);
}
