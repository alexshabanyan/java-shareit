package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserStorage {
    User create(User user);

    User get(Long id);

    Collection<User> getAll();

    User update(User user);

    boolean delete(Long id);
}
