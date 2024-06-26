package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.model.Request;

import java.util.List;

public interface RequestService {
    Request createRequest(RequestDto requestDto, Long userId);

    List<Request> getOwnRequests(Long userId, int from, int size);

    List<Request> getAllRequests(Long userId, int from, int size);

    Request getRequest(Long userId, Long requestId);
}
