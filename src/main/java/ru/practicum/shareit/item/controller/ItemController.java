package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemExtendDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.utils.Headers;
import ru.practicum.shareit.validation.ValidationGroup;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    @Validated({ValidationGroup.OnCreate.class})
    public ItemDto createItem(@RequestHeader(Headers.HEADER_USER_ID) Long userId,
                              @Valid @RequestBody ItemDto itemDto) {
        log.info("Создание предмета itemDto={}", itemDto);
        return itemService.create(itemDto, userId);
    }

    @GetMapping("/{itemId}")
    public ItemExtendDto getItem(@PathVariable Long itemId, @RequestHeader(Headers.HEADER_USER_ID) Long userId) {
        log.info("Получение предмета itemId={}", itemId);
        return itemService.get(itemId, userId);
    }

    @GetMapping
    public Collection<ItemExtendDto> getAllUserItems(@RequestHeader(Headers.HEADER_USER_ID) Long userId) {
        log.info("Получение всех предметов пользователя userId={}", userId);
        return itemService.getAll(userId);
    }

    @PatchMapping("/{itemId}")
    @Validated({ValidationGroup.OnUpdate.class})
    public ItemDto updateItem(@PathVariable Long itemId,
                              @Valid @RequestBody ItemDto itemDto,
                              @RequestHeader(Headers.HEADER_USER_ID) Long userId) {
        log.info("Обновление предмета пользователя userId={}, itemId={}, itemDto={}", userId, itemId, itemDto);
        return itemService.update(itemDto, itemId, userId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> searchAvailableItems(@RequestParam String text) {
        log.info("Поиск доступных предметов text={}", text);
        if (text.isBlank()) {
            return List.of();
        }
        return itemService.searchAvailableItems(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestBody @Valid CommentDto commentDto,
                                    @PathVariable Long itemId,
                                    @RequestHeader(Headers.HEADER_USER_ID) Long userId) {
        log.info("Создание отзыва comment={} на предмет itemId={} от пользователя userId={}", commentDto, itemId, userId);
        return itemService.createComment(commentDto, itemId, userId);
    }
}
