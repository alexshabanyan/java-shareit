package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.args.CreateRequestArgs;
import ru.practicum.shareit.request.model.Request;

import java.util.List;

public interface RequestService {
    Request createRequest(CreateRequestArgs createRequestArgs);

    List<Request> getOwnRequests(Long userId, int from, int size);

    List<Request> getAllRequests(Long userId, int from, int size);

    Request getRequest(Long userId, Long requestId);
}
