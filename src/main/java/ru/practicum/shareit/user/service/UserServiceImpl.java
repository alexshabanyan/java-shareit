package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Override
    public UserDto create(UserDto userDto) {
        User user = UserMapper.toModel(userDto);
        User newUser = userStorage.create(user);
        return UserMapper.toDto(newUser);
    }

    @Override
    public UserDto get(Long userId) {
        User user = userStorage.get(userId);
        return UserMapper.toDto(user);
    }

    @Override
    public Collection<UserDto> getAll() {
        return userStorage.getAll().stream().map(UserMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public UserDto update(UserDto userDto) {
        User user = UserMapper.toModel(userDto);
        User updatedUser = userStorage.update(user);
        return UserMapper.toDto(updatedUser);
    }

    @Override
    public void delete(Long userId) {
        userStorage.delete(userId);
    }
}
