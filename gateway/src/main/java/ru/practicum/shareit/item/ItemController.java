package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.utils.Headers;
import ru.practicum.shareit.validation.ValidationGroup;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    @Validated({ValidationGroup.OnCreate.class})
    public ResponseEntity<Object> createItem(@RequestHeader(Headers.HEADER_USER_ID) Long userId,
                                             @Valid @RequestBody ItemDto itemDto) {
        log.info("Создание предмета itemDto={}", itemDto);
        return itemClient.createItem(userId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@RequestHeader(Headers.HEADER_USER_ID) Long userId,
                                          @PathVariable Long itemId) {
        log.info("Получение предмета itemId={}", itemId);
        return itemClient.getItem(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUserItems(@RequestHeader(Headers.HEADER_USER_ID) Long userId,
                                                  @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                  @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Получение всех предметов пользователя userId={}", userId);
        return itemClient.getAllUserItems(userId, from, size);
    }

    @PatchMapping("/{itemId}")
    @Validated({ValidationGroup.OnUpdate.class})
    public ResponseEntity<Object> updateItem(@RequestHeader(Headers.HEADER_USER_ID) Long userId,
                                             @PathVariable Long itemId,
                                             @Valid @RequestBody ItemDto itemDto) {
        log.info("Обновление предмета пользователя userId={}, itemId={}, itemDto={}", userId, itemId, itemDto);
        return itemClient.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchAvailableItems(@RequestHeader(Headers.HEADER_USER_ID) Long userId,
                                                       @RequestParam String text,
                                                       @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                       @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Поиск доступных предметов text={}", text);
        if (text.isBlank()) {
            return ResponseEntity.ok(List.of());
        }
        return itemClient.searchAvailableItems(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader(Headers.HEADER_USER_ID) Long userId,
                                                @PathVariable Long itemId,
                                                @RequestBody @Valid CommentDto commentDto) {
        log.info("Создание отзыва comment={} на предмет itemId={} от пользователя userId={}", commentDto, itemId, userId);
        return itemClient.createComment(userId, itemId, commentDto);
    }
}
