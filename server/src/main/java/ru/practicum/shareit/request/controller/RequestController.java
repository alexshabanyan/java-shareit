package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
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
@Slf4j
public class RequestController {
    private final RequestMapper requestMapper;
    private final RequestService requestService;
    private final ItemService itemService;

    @PostMapping
    RequestDto createRequest(@RequestHeader(Headers.HEADER_USER_ID) Long userId,
                             @RequestBody RequestDto requestDto) {
        log.info("Создание запроса requestDto={}", requestDto);
        Request request = requestService.createRequest(requestMapper.toCreateRequestArgs(requestDto, userId));
        return requestMapper.toDto(request);
    }

    @GetMapping
    Collection<RequestWithItemInfoDto> getOwnRequests(@RequestHeader(Headers.HEADER_USER_ID) Long userId,
                                                      @RequestParam Integer from,
                                                      @RequestParam Integer size) {
        log.info("Получение списка своих запросов для пользователя userId={}, from={}, size={}, ", from, size, userId);
        List<Request> requests = requestService.getOwnRequests(userId, from, size);
        Set<Long> requestIds = requests.stream().map(Request::getId).collect(Collectors.toSet());
        Map<Long, List<Item>> requestItemMapping = itemService.getRequestItemMapping(requestIds);
        return requestMapper.toRequestWithItemInfoDto(requests, requestItemMapping);
    }

    @GetMapping("/all")
    Collection<RequestWithItemInfoDto> getAllRequests(@RequestHeader(Headers.HEADER_USER_ID) Long userId,
                                                      @RequestParam Integer from,
                                                      @RequestParam Integer size) {
        log.info("Получение списка чужих запросов для пользователя userId={}, from={}, size={}", userId, from, size);
        List<Request> requests = requestService.getAllRequests(userId, from, size);
        Set<Long> requestIds = requests.stream().map(Request::getId).collect(Collectors.toSet());
        Map<Long, List<Item>> requestItemMapping = itemService.getRequestItemMapping(requestIds);
        return requestMapper.toRequestWithItemInfoDto(requests, requestItemMapping);
    }

    @GetMapping("/{requestId}")
    RequestWithItemInfoDto getRequest(@RequestHeader(Headers.HEADER_USER_ID) Long userId,
                                      @PathVariable Long requestId) {
        log.info("Получение запроса requestId={}, userId={}", requestId, userId);
        Request request = requestService.getRequest(userId, requestId);
        Map<Long, List<Item>> requestItemMapping = itemService.getRequestItemMapping(Set.of(request.getId()));
        return requestMapper.toRequestWithItemInfoDto(List.of(request), requestItemMapping)
                .stream().findFirst().orElseThrow(() -> new RuntimeException("Ошибка при мапинге RequestWithItemInfoDto"));
    }
}
