package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.UserMapper;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.validation.ValidationGroup;

import javax.validation.Valid;
import java.util.Collection;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping
    @Validated({ValidationGroup.OnCreate.class})
    public UserDto createUser(@Valid @RequestBody UserDto userDto) {
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
    @Validated({ValidationGroup.OnUpdate.class})
    public UserDto updateUser(@PathVariable Long userId, @Valid @RequestBody UserDto userDto) {
        log.info("Обновление пользователя userDto={}", userDto);
        return userMapper.toDto(userService.update(userMapper.toModel(userDto), userId));
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        log.info("Удаление пользователя userId={}", userId);
        userService.delete(userId);
    }
}


