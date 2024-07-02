package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.utils.Headers;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
@Slf4j
public class RequestController {
    private final RequestClient requestClient;

    @PostMapping
    ResponseEntity<Object> createRequest(@RequestHeader(Headers.HEADER_USER_ID) Long userId,
                                         @Valid @RequestBody RequestDto requestDto) {
        log.info("Создание запроса requestDto={}", requestDto);
        return requestClient.createRequest(userId, requestDto);
    }

    @GetMapping
    ResponseEntity<Object> getOwnRequests(@RequestHeader(Headers.HEADER_USER_ID) Long userId,
                                          @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                          @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Получение списка своих запросов для пользователя userId={}, from={}, size={}, ", from, size, userId);
        return requestClient.getOwnRequests(userId, from, size);
    }

    @GetMapping("/all")
    ResponseEntity<Object> getAllRequests(@RequestHeader(Headers.HEADER_USER_ID) Long userId,
                                          @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                          @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Получение списка чужих запросов для пользователя userId={}, from={}, size={}", userId, from, size);
        return requestClient.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    ResponseEntity<Object> getRequest(@RequestHeader(Headers.HEADER_USER_ID) Long userId,
                                      @PathVariable Long requestId) {
        log.info("Получение запроса requestId={}, userId={}", requestId, userId);
        return requestClient.getRequest(userId, requestId);
    }
}
