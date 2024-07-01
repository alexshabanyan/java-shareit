package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.validation.ValidationGroup;

import javax.validation.Valid;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;

    @PostMapping
    @Validated({ValidationGroup.OnCreate.class})
    public ResponseEntity<Object> createUser(@Valid @RequestBody UserDto userDto) {
        log.info("Создание пользователя userDto={}", userDto);
        return userClient.createUser(userDto);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUser(@PathVariable Long userId) {
        log.info("Получение пользователя userId={}", userId);
        return userClient.getUser(userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("Получение списка всех пользователей");
        return userClient.getAllUsers();
    }

    @PatchMapping("/{userId}")
    @Validated({ValidationGroup.OnUpdate.class})
    public ResponseEntity<Object> updateUser(@PathVariable Long userId, @Valid @RequestBody UserDto userDto) {
        log.info("Обновление пользователя userDto={}", userDto);
        return userClient.updateUser(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable Long userId) {
        log.info("Удаление пользователя userId={}", userId);
        return userClient.deleteUser(userId);
    }
}


