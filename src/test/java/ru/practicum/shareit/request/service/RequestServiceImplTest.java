package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.args.CreateRequestArgs;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.storage.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class RequestServiceImplTest {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final RequestService requestService;

    User userWithRequests;
    User userWithoutRequests;

    @BeforeEach
    void setUp() {
        userWithoutRequests = userRepository.save(new User(null, "User 1 name", "user1@mail.com"));
        userWithRequests = userRepository.save(new User(null, "User 2 name", "user2@mail.com"));
    }

    @Test
    void shouldCreateAndGetRequestsByCorrectUsers() {
        Request request = requestService.createRequest(new CreateRequestArgs(userWithRequests.getId(), "Request desc 1"));
        Request savedRequest = requestRepository.findById(request.getId()).orElseThrow();
        assertEquals(savedRequest, request);
        assertEquals(savedRequest, requestService.getRequest(userWithRequests.getId(), request.getId()));

        List<Request> ownRequests = requestService.getOwnRequests(userWithRequests.getId(), 0, 10);
        assertThat(ownRequests, contains(request));

        List<Request> ownRequestsEmpty = requestService.getOwnRequests(userWithoutRequests.getId(), 0, 10);
        assertThat(ownRequestsEmpty, hasSize(0));

        List<Request> allRequests = requestService.getAllRequests(userWithoutRequests.getId(), 0, 10);
        assertThat(allRequests, contains(request));
    }
}