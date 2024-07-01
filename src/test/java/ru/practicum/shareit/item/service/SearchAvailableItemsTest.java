package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SearchAvailableItemsTest {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemService itemService;

    private Item availableItem1;
    private Item availableItem2;

    @BeforeAll
    void setUp() {
        User owner = userRepository.save(new User(null, "User 1", "user1@mail.com"));
        availableItem1 = itemRepository.save(new Item(null, "Item 1 name", "Item 1 desc", true, owner.getId(), null));
        availableItem2 = itemRepository.save(new Item(null, "Item 2 name", "Item 2 desc", true, owner.getId(), null));
        Item notAvailableItem = itemRepository.save(new Item(null, "Item na name", "Item na desc", false, owner.getId(), null));
    }

    @AfterAll
    void afterAll() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
    }

    @ParameterizedTest
    @MethodSource("argumentsForSearchAvailableItems")
    void searchAvailableItems(String text, List<Item> expectedItems) {
        final int from = 0;
        final int size = 10;
        Set<Long> itemIds = itemService.searchAvailableItems(text, from, size).stream().map(Item::getId).collect(Collectors.toSet());
        Set<Long> expectedIds = expectedItems.stream().map(Item::getId).collect(Collectors.toSet());
        assertEquals(expectedIds, itemIds);
        assertThat(itemIds, containsInAnyOrder(expectedIds.toArray()));
    }

    Stream<Arguments> argumentsForSearchAvailableItems() {
        return Stream.of(
                Arguments.of("ItEm 2 desc", List.of(availableItem2)),
                Arguments.of("IteM 1 N", List.of(availableItem1)),
                Arguments.of("DeSc", List.of(availableItem1, availableItem2)),
                Arguments.of("shouldNotFindAnything", List.of())
        );
    }
}