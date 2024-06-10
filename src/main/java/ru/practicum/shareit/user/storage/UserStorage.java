package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserStorage {
    User create(User user);

    User getById(Long id);

    Collection<User> getAll();

    User update(User user);

    boolean delete(Long id);

    boolean isExist(Long id);
}
