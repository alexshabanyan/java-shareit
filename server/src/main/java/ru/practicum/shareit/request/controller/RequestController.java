package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestWithItemInfoDto;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.model.RequestMapper;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.utils.Headers;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
@Slf4j
public class RequestController {
    private final RequestMapper requestMapper;
    private final RequestService requestService;
    private final ItemService itemService;

    @PostMapping
    RequestDto createRequest(@RequestHeader(Headers.HEADER_USER_ID) Long userId,
                             @RequestBody RequestDto requestDto) {
        log.info("Создание запроса requestDto={}", requestDto);
        Request request = requestService.createRequest(requestDto, userId);
        return requestMapper.toDto(request);
    }

    @GetMapping
    Collection<RequestWithItemInfoDto> getOwnRequests(@RequestParam(defaultValue = "0") Integer from,
                                                      @RequestParam(defaultValue = "10") Integer size,
                                                      @RequestHeader(Headers.HEADER_USER_ID) Long userId) {
        log.info("Получение списка своих запросов для пользователя userId={}, from={}, size={}, ", from, size, userId);
        List<Request> requests = requestService.getOwnRequests(userId, from, size);
        Set<Long> requestIds = requests.stream().map(Request::getId).collect(Collectors.toSet());
        Map<Long, List<Item>> requestItemMapping = itemService.getRequestItemMapping(requestIds);
        return requestMapper.toRequestWithItemInfoDto(requests, requestItemMapping);
    }

    @GetMapping("/all")
    Collection<RequestWithItemInfoDto> getAllRequests(@RequestParam(defaultValue = "0") Integer from,
                                                      @RequestParam(defaultValue = "10") Integer size,
                                                      @RequestHeader(Headers.HEADER_USER_ID) Long userId) {
        log.info("Получение списка чужих запросов для пользователя userId={}, from={}, size={}", userId, from, size);
        List<Request> requests = requestService.getAllRequests(userId, from, size);
        Set<Long> requestIds = requests.stream().map(Request::getId).collect(Collectors.toSet());
        Map<Long, List<Item>> requestItemMapping = itemService.getRequestItemMapping(requestIds);
        return requestMapper.toRequestWithItemInfoDto(requests, requestItemMapping);
    }

    @GetMapping("/{requestId}")
    RequestWithItemInfoDto getRequest(@PathVariable Long requestId,
                                      @RequestHeader(Headers.HEADER_USER_ID) Long userId) {
        log.info("Получение запроса requestId={}, userId={}", requestId, userId);
        Request request = requestService.getRequest(userId, requestId);
        Map<Long, List<Item>> requestItemMapping = itemService.getRequestItemMapping(Set.of(request.getId()));
        return requestMapper.toRequestWithItemInfoDto(List.of(request), requestItemMapping)
                .stream().findFirst().orElseThrow(() -> new RuntimeException("Ошибка при мапинге RequestWithItemInfoDto"));
    }
}
