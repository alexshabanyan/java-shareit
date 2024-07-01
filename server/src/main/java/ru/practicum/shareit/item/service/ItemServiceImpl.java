package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotAllowedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.args.CreateCommentArgs;
import ru.practicum.shareit.item.args.CreateItemArgs;
import ru.practicum.shareit.item.args.UpdateItemArgs;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;
import ru.practicum.shareit.utils.Pagination;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemMapper itemMapper;

    @Override
    @Transactional
    public Item create(CreateItemArgs args) {
        userRepository.findById(args.getOwnerId()).orElseThrow(() -> new NotFoundException(args.getOwnerId()));
        return itemRepository.save(itemMapper.toModel(args));
    }

    @Override
    public Item get(Long id, Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException(userId));
        return itemRepository.findById(id, Item.class).orElseThrow(() -> new NotFoundException(id));
    }

    @Override
    public List<Item> getAll(Long userId, int from, int size) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException(userId));
        Pageable page = Pagination.getPage(from, size);
        return itemRepository.findAllByOwnerId(userId, page, Item.class);
    }

    @Override
    @Transactional
    public Item update(UpdateItemArgs args, Long itemId, Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException(userId));
        Item savedItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(itemId));
        if (!Objects.equals(userId, savedItem.getOwnerId())) {
            throw new NotAllowedException("Изменять предмет может только владелец");
        }
        itemMapper.updateModel(savedItem, args);
        return itemRepository.save(savedItem);
    }

    @Override
    public List<Item> searchAvailableItems(String text, int from, int size) {
        Pageable page = Pagination.getPage(from, size);
        return itemRepository.findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailable(text,
                text, true, page);
    }

    @Override
    @Transactional
    public Comment createComment(CreateCommentArgs args) {
        User user = userRepository.findById(args.getUserId()).orElseThrow(() -> new NotFoundException(args.getUserId()));
        Comment savedComment = commentRepository.findByAuthorIdAndItemId(args.getUserId(), args.getItemId());
        if (savedComment != null) {
            throw new ValidationException("Отзыв уже существует");
        }
        List<Booking> bookings = bookingRepository.findAllByItemIdAndBookerIdAndEndBefore(args.getItemId(), args.getUserId(), LocalDateTime.now());
        bookings.stream().filter(b -> b.getStatus() == BookingStatus.APPROVED).findFirst().orElseThrow(() ->
                new ValidationException("Пользователь не бронировал этот предмет для оставления отзывов"));

        return commentRepository.save(itemMapper.toModel(args, user));
    }

    @Override
    public Map<Long, List<Comment>> getItemCommentMapping(Set<Long> itemIds) {
        Map<Long, List<Comment>> commentMapping;
        commentMapping = commentRepository.findAllByItemIdIn(itemIds)
                .stream().collect(Collectors.groupingBy(Comment::getItemId));
        return commentMapping;
    }

    @Override
    public Map<Long, List<Item>> getRequestItemMapping(Set<Long> requestIds) {
        return itemRepository.findAllByRequestIdIn(requestIds)
                .stream().collect(Collectors.groupingBy(Item::getRequestId));
    }
}
