package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithExtendInfoDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.utils.Headers;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemMapper itemMapper;
    private final ItemService itemService;
    private final BookingService bookingService;

    @PostMapping
    public ItemDto createItem(@RequestHeader(Headers.HEADER_USER_ID) Long userId,
                              @RequestBody ItemDto itemDto) {
        log.info("Создание предмета itemDto={}", itemDto);
        Item item = itemService.create(itemDto, userId);
        return itemMapper.toDto(item);
    }

    @GetMapping("/{itemId}")
    public ItemWithExtendInfoDto getItem(@PathVariable Long itemId,
                                         @RequestHeader(Headers.HEADER_USER_ID) Long userId) {
        log.info("Получение предмета itemId={}", itemId);
        Item item = itemService.get(itemId, userId);
        Map<Long, List<Comment>> commentsMapping = itemService.getItemCommentMapping(Set.of(item.getId()));
        Map<Long, List<Booking>> lastBookingMapping = null;
        Map<Long, List<Booking>> nextBookingMapping = null;
        if (Objects.equals(userId, item.getOwnerId())) {
            lastBookingMapping = bookingService.getItemLastBookingMapping(Set.of(item.getId()));
            nextBookingMapping = bookingService.getItemNextBookingMapping(Set.of(item.getId()));
        }
        return itemMapper.toExtendInfoDto(List.of(item), commentsMapping, lastBookingMapping, nextBookingMapping)
                .stream().findFirst().orElseThrow(() -> new RuntimeException("Ошибка маппинга ItemWithExtendInfoDto"));
    }

    @GetMapping
    public Collection<ItemWithExtendInfoDto> getAllUserItems(@RequestHeader(Headers.HEADER_USER_ID) Long userId,
                                                             @RequestParam Integer from,
                                                             @RequestParam Integer size) {
        log.info("Получение всех предметов пользователя userId={}", userId);
        List<Item> allItems = itemService.getAll(userId, from, size).stream().sorted(Comparator.comparing(Item::getId)).collect(Collectors.toList());
        Set<Long> itemIds = allItems.stream().map(Item::getId).collect(Collectors.toSet());
        Map<Long, List<Comment>> commentsMapping = itemService.getItemCommentMapping(itemIds);
        Map<Long, List<Booking>> lastBookingMapping = bookingService.getItemLastBookingMapping(itemIds);
        Map<Long, List<Booking>> nextBookingMapping = bookingService.getItemNextBookingMapping(itemIds);
        return itemMapper.toExtendInfoDto(allItems, commentsMapping, lastBookingMapping, nextBookingMapping);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable Long itemId,
                              @RequestBody ItemDto itemDto,
                              @RequestHeader(Headers.HEADER_USER_ID) Long userId) {
        log.info("Обновление предмета пользователя userId={}, itemId={}, itemDto={}", userId, itemId, itemDto);
        Item updatedItem = itemService.update(itemDto, itemId, userId);
        return itemMapper.toDto(updatedItem);
    }

    @GetMapping("/search")
    public Collection<ItemDto> searchAvailableItems(@RequestParam String text,
                                                    @RequestParam Integer from,
                                                    @RequestParam Integer size) {
        log.info("Поиск доступных предметов text={}", text);
        return itemService.searchAvailableItems(text, from, size).stream().map(itemMapper::toDto).collect(Collectors.toList());
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestBody CommentDto commentDto,
                                    @PathVariable Long itemId,
                                    @RequestHeader(Headers.HEADER_USER_ID) Long userId) {
        log.info("Создание отзыва comment={} на предмет itemId={} от пользователя userId={}", commentDto, itemId, userId);
        Comment comment = itemService.createComment(commentDto, itemId, userId);
        return itemMapper.toDto(comment);
    }
}
