package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingForItemExtendDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotAllowedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemExtendDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;
    private final BookingMapper bookingMapper;

    @Override
    @Transactional
    public ItemDto create(ItemDto itemDto, Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException(userId));
        Item item = itemMapper.toModel(itemDto, userId);
        Item savedItem = itemRepository.save(item);
        return itemMapper.toDto(savedItem);
    }

    @Override
    @Transactional(readOnly = true)
    public ItemExtendDto get(Long id, Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException(userId));
        Item item = itemRepository.findById(id, Item.class).orElseThrow(() -> new NotFoundException(id));
        boolean withBooking = Objects.equals(userId, item.getOwnerId());
        return getItemsExtendedInfo(List.of(item), true, withBooking).stream().findFirst()
                .orElseThrow(() -> new NotFoundException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<ItemExtendDto> getAll(Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException(userId));
        List<Item> items = itemRepository.findAllByOwnerId(userId, Item.class);
        return getItemsExtendedInfo(items, true, true);
    }

    @Override
    @Transactional
    public ItemDto update(ItemDto itemDto, Long itemId, Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException(userId));
        Item savedItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(itemId));
        if (!Objects.equals(userId, savedItem.getOwnerId())) {
            throw new NotAllowedException("Изменять предмет может только владелец");
        }
        itemMapper.updateModel(savedItem, itemDto);
        return itemMapper.toDto(itemRepository.save(savedItem));
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<ItemDto> searchAvailableItems(String text) {
        return itemRepository.findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailable(text,
                text, true, ItemDto.class);
    }

    @Override
    @Transactional
    public CommentDto createComment(CommentDto commentDto, Long itemId, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(userId));
        Comment comment = commentRepository.findByAuthorIdAndItemId(userId, itemId, Comment.class);
        if (comment != null) {
            throw new ValidationException("Отзыв уже существует");
        }
        List<Booking> bookings = bookingRepository.findAllByItemIdAndBookerIdAndEndBefore(itemId, userId, LocalDateTime.now());
        bookings.stream().filter(b -> b.getStatus() == BookingStatus.APPROVED).findFirst().orElseThrow(() ->
                new ValidationException("Пользователь не бронировал этот предмет для оставления отзывов"));
        return commentMapper.toDto(commentRepository.save(commentMapper.toModel(commentDto, user, itemId)));
    }

    private Collection<ItemExtendDto> getItemsExtendedInfo(List<Item> items, boolean withComments, boolean withBooking) {
        Set<Long> itemIds = items.stream().map(Item::getId).collect(Collectors.toSet());

        Map<Long, List<CommentDto>> commentMapping = null;
        if (withComments) {
            commentMapping = commentRepository.findAllByItemIdIn(itemIds, CommentDto.class)
                    .stream().collect(Collectors.groupingBy(CommentDto::getItemId));
        }

        Map<Long, List<BookingForItemExtendDto>> nextBookingMapping = null;
        Map<Long, List<BookingForItemExtendDto>> lastBookingMapping = null;
        if (withBooking) {
            LocalDateTime now = LocalDateTime.now();
            nextBookingMapping = bookingRepository
                    .findNextBookingWithStatus(itemIds, now, BookingStatus.APPROVED, Booking.class).stream()
                    .map(bookingMapper::toItemExtendDto)
                    .collect(Collectors.groupingBy(BookingForItemExtendDto::getItemId));
            lastBookingMapping = bookingRepository
                    .findLastBookingWithStatus(itemIds, now, BookingStatus.APPROVED, Booking.class).stream()
                    .map(bookingMapper::toItemExtendDto)
                    .collect(Collectors.groupingBy(BookingForItemExtendDto::getItemId));
        }

        Collection<ItemExtendDto> itemExtendDtos = new ArrayList<>();
        for (Item item : items) {
            List<CommentDto> commentsList = new ArrayList<>();
            if (commentMapping != null && commentMapping.get(item.getId()) != null) {
                commentsList = commentMapping.get(item.getId());
            }
            BookingForItemExtendDto last = null;
            if (lastBookingMapping != null && lastBookingMapping.containsKey(item.getId())) {
                last = lastBookingMapping.get(item.getId()).stream().findFirst().orElse(null);
            }
            BookingForItemExtendDto next = null;
            if (nextBookingMapping != null && nextBookingMapping.containsKey(item.getId())) {
                next = nextBookingMapping.get(item.getId()).stream().findFirst().orElse(null);
            }
            itemExtendDtos.add(itemMapper.toItemBookingInfoDto(item, commentsList, last, next));
        }
        return itemExtendDtos;
    }
}
