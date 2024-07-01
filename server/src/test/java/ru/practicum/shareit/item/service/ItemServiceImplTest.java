package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotAllowedException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.args.CreateCommentArgs;
import ru.practicum.shareit.item.args.CreateItemArgs;
import ru.practicum.shareit.item.args.UpdateItemArgs;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.storage.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceImplTest {
    private final RequestRepository requestRepository;
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final ItemService itemService;

    @Test
    void shouldCreateAndGetAndUpdateItem() {
        User otherUser = userRepository.save(new User(null, "User 1 name", "user1@mail.com"));
        User owner = userRepository.save(new User(null, "User 2 name", "user2@mail.com"));
        Request request = requestRepository.save(new Request(null, "desc", null, owner.getId()));
        Item item = itemService.create(new CreateItemArgs(owner.getId(), "Item name", "Item desc", true, request.getId()));
        Item savedItem = itemRepository.findById(item.getId()).orElseThrow();

        assertEquals(savedItem, item);
        assertEquals(savedItem, itemService.get(item.getId(), owner.getId()));
        assertThat(itemService.getAll(owner.getId(), 0, 10), contains(item));

        itemService.update(new UpdateItemArgs(null, "Updated desc", null), item.getId(), owner.getId());
        Item updatedItem = itemRepository.findById(item.getId()).orElseThrow();

        assertEquals("Item name", updatedItem.getName());
        assertEquals("Updated desc", updatedItem.getDescription());
        assertThrows(NotAllowedException.class, () -> itemService.update(new UpdateItemArgs(null, "Updated desc", null), item.getId(), otherUser.getId()));
    }

    @Test
    void shouldGetCorrectMappings() {
        User booker = userRepository.save(new User(null, "User 1 name", "user1@mail.com"));
        User owner = userRepository.save(new User(null, "User 2 name", "user2@mail.com"));
        Request request = requestRepository.save(new Request(null, "desc", null, owner.getId()));
        Item item = itemService.create(new CreateItemArgs(owner.getId(), "Item name", "Item desc", true, request.getId()));
        bookingRepository.save(new Booking(null, item, BookingStatus.APPROVED, booker, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1)));
        Comment comment = commentRepository.save(new Comment(null, item.getId(), booker, "text", null));

        Map<Long, List<Comment>> itemCommentMapping = itemService.getItemCommentMapping(Set.of(item.getId()));

        assertThat(itemCommentMapping.entrySet(), hasSize(1));
        assertThat(itemCommentMapping.get(item.getId()), equalTo(List.of(comment)));

        Map<Long, List<Item>> requestItemMapping = itemService.getRequestItemMapping(Set.of(request.getId()));

        assertThat(requestItemMapping.entrySet(), hasSize(1));
        assertThat(requestItemMapping.get(request.getId()), equalTo(List.of(item)));
    }

    @Test
    void shouldCreateCommentByCorrectUsers() {
        User booker = userRepository.save(new User(null, "User 1 name", "user1@mail.com"));
        User owner = userRepository.save(new User(null, "User 2 name", "user2@mail.com"));
        Item item = itemService.create(new CreateItemArgs(owner.getId(), "Item name", "Item desc", true, null));
        bookingRepository.save(new Booking(null, item, BookingStatus.APPROVED, booker, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1)));

        assertThrows(ValidationException.class, () -> itemService.createComment(new CreateCommentArgs(owner.getId(), item.getId(), "text")));
        Comment comment = itemService.createComment(new CreateCommentArgs(booker.getId(), item.getId(), "text"));
        Comment savedComment = commentRepository.findById(comment.getId()).orElseThrow();
        assertEquals(savedComment, comment);
        assertThrows(ValidationException.class, () -> itemService.createComment(new CreateCommentArgs(booker.getId(), item.getId(), "text")));
    }
}