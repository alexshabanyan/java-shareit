package ru.practicum.shareit.item.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotAllowedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemMapper;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class ItemStorageInMemory implements ItemStorage {
    private final Map<Long, Item> items = new HashMap<>();
    private long id = 1;

    private long generateId() {
        return id++;
    }

    @Override
    public Item create(Item item) {
        long id = generateId();
        Item updatedItem = item.toBuilder().id(id).build();
        items.put(id, updatedItem);
        log.info("Предмет создан item={}", updatedItem);
        return updatedItem;
    }

    @Override
    public Item get(Long id) {
        Item item = items.get(id);
        if (item == null) {
            throw new NotFoundException(id);
        }
        return item;
    }

    @Override
    public Collection<Item> getAll(User user) {
        return items.values().stream()
                .filter(i -> Objects.equals(i.getOwnerId(), user.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public Item update(Item item) {
        Item oldItem = items.get(item.getId());
        if (oldItem == null) {
            throw new NotFoundException(item.getId());
        }
        if (!Objects.equals(oldItem.getOwnerId(), item.getOwnerId())) {
            throw new NotAllowedException("Изменять предмет может только владелец");
        }
        Item updatedItem = ItemMapper.updateModel(oldItem, item);
        items.put(item.getId(), updatedItem);
        return updatedItem;
    }

    @Override
    public Collection<Item> searchAvailableItems(String text) {
        String lcText = text.toLowerCase();
        return items.values().stream()
                .filter(Item::getAvailable)
                .filter(i -> i.getName().toLowerCase().contains(lcText) ||
                        i.getDescription().toLowerCase().contains(lcText))
                .collect(Collectors.toList());
    }
}
