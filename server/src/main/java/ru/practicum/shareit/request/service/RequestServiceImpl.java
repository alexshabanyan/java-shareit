package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.args.CreateRequestArgs;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.model.RequestMapper;
import ru.practicum.shareit.request.storage.RequestRepository;
import ru.practicum.shareit.user.storage.UserRepository;
import ru.practicum.shareit.utils.Pagination;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final RequestMapper requestMapper;

    @Override
    @Transactional
    public Request createRequest(CreateRequestArgs args) {
        userRepository.findById(args.getUserId()).orElseThrow(() -> new NotFoundException(args.getUserId()));
        return requestRepository.save(requestMapper.toModel(args));
    }

    @Override
    public List<Request> getOwnRequests(Long userId, int from, int size) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException(userId));
        Pageable page = Pagination.getPage(from, size, Sort.by("created").descending());
        return requestRepository.findAllByUserId(userId, page);
    }

    @Override
    public List<Request> getAllRequests(Long userId, int from, int size) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException(userId));
        Pageable page = Pagination.getPage(from, size, Sort.by("created").descending());
        return requestRepository.findAllByUserIdNot(userId, page);
    }

    @Override
    public Request getRequest(Long userId, Long requestId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException(userId));
        return requestRepository.findById(requestId).orElseThrow(() -> new NotFoundException(requestId));
    }
}
