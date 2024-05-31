package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    public ItemDto create(ItemDto itemDto, Long userId) {
        userStorage.get(userId);
        Item item = ItemMapper.toModel(itemDto, userId);
        Item newItem = itemStorage.create(item);
        return ItemMapper.toDto(newItem);
    }

    @Override
    public ItemDto get(Long id) {
        Item item = itemStorage.get(id);
        return ItemMapper.toDto(item);
    }

    @Override
    public Collection<ItemDto> getAll(Long userId) {
        return itemStorage.getAll(userStorage.get(userId)).stream().map(ItemMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public ItemDto update(ItemDto itemDto, Long userId) {
        Item item = ItemMapper.toModel(itemDto, userId);
        Item updatedItem = itemStorage.update(item);
        return ItemMapper.toDto(updatedItem);
    }

    @Override
    public Collection<ItemDto> searchAvailableItems(String text) {
        return itemStorage.searchAvailableItems(text).stream().map(ItemMapper::toDto).collect(Collectors.toList());
    }
}
