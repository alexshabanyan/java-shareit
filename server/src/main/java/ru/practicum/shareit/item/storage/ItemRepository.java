package ru.practicum.shareit.item.storage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ItemRepository extends JpaRepository<Item, Long> {
    <T> Optional<T> findById(Long itemId, Class<T> type);

    <T> List<T> findAllByOwnerId(Long id, Pageable page, Class<T> type);

    List<Item> findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailable(String searchNameBy,
                                                                                              String searchDescBy,
                                                                                              Boolean available,
                                                                                              Pageable page);

    List<Item> findAllByRequestIdIn(Set<Long> requestIds);
}
