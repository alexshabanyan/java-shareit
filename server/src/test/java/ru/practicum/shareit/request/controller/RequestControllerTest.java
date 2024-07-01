package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.model.RequestMapper;
import ru.practicum.shareit.request.model.RequestMapperImpl;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.utils.Headers;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@WebMvcTest(controllers = RequestController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import(RequestMapperImpl.class)
class RequestControllerTest {
    private final MockMvc mvc;
    private final ObjectMapper mapper;

    @MockBean
    private RequestService requestService;
    @MockBean
    private ItemService itemService;

    private static RequestMapper requestMapper;
    private Request request;
    private User user;

    @BeforeAll
    static void beforeAll() {
        requestMapper = new RequestMapperImpl();
    }

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        request = new Request();
        request.setId(3L);
    }

    @Test
    void createRequest() throws Exception {
        final long userId = user.getId();
        when(requestService.createRequest(any())).thenReturn(request);

        RequestDto requestDto = RequestDto.builder().description("Desc").build();
        assertThat(performCreateRequest(userId, requestDto).getStatus(), is(200));
        verify(requestService, times(1)).createRequest(requestMapper.toCreateRequestArgs(requestDto, userId));
    }

    private MockHttpServletResponse performCreateRequest(Long userId, RequestDto requestDto) throws Exception {
        MvcResult mvcResult = mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(requestDto))
                        .header(Headers.HEADER_USER_ID, userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        return mvcResult.getResponse();
    }

    @Test
    void getOwnRequests() throws Exception {
        final long userId = user.getId();
        when(requestService.getOwnRequests(any(), anyInt(), anyInt())).thenReturn(List.of(request));
        Map<Long, List<Item>> requestItemMapping = new HashMap<>();
        requestItemMapping.put(request.getId(), new ArrayList<>());
        when(itemService.getRequestItemMapping(anySet())).thenReturn(requestItemMapping);

        assertThat(performGetOwnRequests(userId, 0, 11).getStatus(), is(200));
        verify(requestService, times(1)).getOwnRequests(userId, 0, 11);
    }

    private MockHttpServletResponse performGetOwnRequests(Long userId) throws Exception {
        MvcResult mvcResult = mvc.perform(get("/requests")
                        .header(Headers.HEADER_USER_ID, userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        return mvcResult.getResponse();
    }

    private MockHttpServletResponse performGetOwnRequests(Long userId, Integer from, Integer size) throws Exception {
        MvcResult mvcResult = mvc.perform(get("/requests")
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .header(Headers.HEADER_USER_ID, userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        return mvcResult.getResponse();
    }

    @Test
    void getAllRequests() throws Exception {
        final long userId = user.getId();
        when(requestService.getAllRequests(any(), anyInt(), anyInt())).thenReturn(List.of(request));
        Map<Long, List<Item>> requestItemMapping = new HashMap<>();
        requestItemMapping.put(request.getId(), new ArrayList<>());
        when(itemService.getRequestItemMapping(anySet())).thenReturn(requestItemMapping);

        assertThat(performGetAllRequests(userId, 0, 11).getStatus(), is(200));
        verify(requestService, times(1)).getAllRequests(userId, 0, 11);
    }

    private MockHttpServletResponse performGetAllRequests(Long userId) throws Exception {
        MvcResult mvcResult = mvc.perform(get("/requests/all")
                        .header(Headers.HEADER_USER_ID, userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        return mvcResult.getResponse();
    }

    private MockHttpServletResponse performGetAllRequests(Long userId, Integer from, Integer size) throws Exception {
        MvcResult mvcResult = mvc.perform(get("/requests/all")
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .header(Headers.HEADER_USER_ID, userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        return mvcResult.getResponse();
    }

    @Test
    void getRequest() throws Exception {
        final long userId = user.getId();
        final long requestId = request.getId();
        when(requestService.getRequest(any(), any())).thenReturn(request);
        Map<Long, List<Item>> requestItemMapping = new HashMap<>();
        requestItemMapping.put(request.getId(), new ArrayList<>());
        when(itemService.getRequestItemMapping(anySet())).thenReturn(requestItemMapping);

        MockHttpServletResponse response = performGetRequest(userId, requestId);
        assertThat(response.getStatus(), is(200));
        assertThat(JsonPath.parse(response.getContentAsString()).read("$.id").toString(), is(String.valueOf(requestId)));
        verify(requestService, times(1)).getRequest(userId, requestId);
    }

    private MockHttpServletResponse performGetRequest(Long userId, Long requestId) throws Exception {
        MvcResult mvcResult = mvc.perform(get("/requests/{requestId}", requestId)
                        .header(Headers.HEADER_USER_ID, userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        return mvcResult.getResponse();
    }
}