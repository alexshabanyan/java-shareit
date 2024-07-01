package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserService {
    User create(User user);

    User get(Long userId);

    Collection<User> getAll();

    User update(User user, Long userId);

    void delete(Long userId);
}
