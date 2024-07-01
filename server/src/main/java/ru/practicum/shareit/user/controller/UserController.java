package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping
    public UserDto createUser(@RequestBody UserDto userDto) {
        log.info("Создание пользователя userDto={}", userDto);
        return userMapper.toDto(userService.create(userMapper.toModel(userDto)));
    }

    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable Long userId) {
        log.info("Получение пользователя userId={}", userId);
        return userMapper.toDto(userService.get(userId));
    }

    @GetMapping
    public Collection<UserDto> getAllUsers() {
        log.info("Получение списка всех пользователей");
        return userService.getAll().stream().map(userMapper::toDto).collect(Collectors.toList());
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable Long userId, @RequestBody UserDto userDto) {
        log.info("Обновление пользователя userDto={}", userDto);
        return userMapper.toDto(userService.update(userMapper.toModel(userDto), userId));
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        log.info("Удаление пользователя userId={}", userId);
        userService.delete(userId);
    }
}


