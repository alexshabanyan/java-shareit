package ru.practicum.shareit.item.controller;

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
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.args.UpdateItemArgs;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemMapper;
import ru.practicum.shareit.item.model.ItemMapperImpl;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.utils.Headers;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(controllers = ItemController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import(ItemMapperImpl.class)
class ItemControllerTest {
    private final MockMvc mvc;
    private final ObjectMapper mapper;

    @MockBean
    ItemService itemService;
    @MockBean
    BookingService bookingService;

    private static ItemMapper itemMapper;
    private Item item;
    private User user;

    @BeforeAll
    static void beforeAll() {
        itemMapper = new ItemMapperImpl();
    }

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        item = new Item();
        item.setId(2L);
        item.setName("Item");
        item.setDescription("Item desc");
        item.setAvailable(true);
        item.setOwnerId(999L);
    }

    @Test
    void createItem() throws Exception {
        final long userId = user.getId();
        when(itemService.create(any())).thenReturn(item);

        assertThat(performCreateItem(userId, "Name", "Desc", true).getStatus(), is(200));
        verify(itemService, times(1)).create(any());
    }

    private MockHttpServletResponse performCreateItem(Long userId, String name, String desc, Boolean available) throws Exception {
        ItemDto itemDto = ItemDto.builder().name(name).description(desc).available(available).build();
        MvcResult mvcResult = mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .header(Headers.HEADER_USER_ID, userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        return mvcResult.getResponse();
    }

    @Test
    void getItem() throws Exception {
        final long userId = user.getId();
        final long itemId = item.getId();
        when(itemService.get(any(), any())).thenReturn(item);
        Map<Long, List<Booking>> bookingMapping = new HashMap<>();
        bookingMapping.put(itemId, List.of(new Booking(1L, item, BookingStatus.APPROVED, user, LocalDateTime.now(), LocalDateTime.now().plusDays(1))));
        when(bookingService.getItemLastBookingMapping(any())).thenReturn(bookingMapping);
        when(bookingService.getItemNextBookingMapping(any())).thenReturn(bookingMapping);

        MockHttpServletResponse response = performGetItem(userId, itemId);
        assertThat(response.getStatus(), is(200));
        assertThat(JsonPath.parse(response.getContentAsString()).read("$.id").toString(), is(String.valueOf(itemId)));
        verify(itemService, times(1)).get(any(), any());

        // by owner
        response = performGetItem(item.getOwnerId(), itemId);
        assertThat(response.getStatus(), is(200));
        assertThat(JsonPath.parse(response.getContentAsString()).read("$.id").toString(), is(String.valueOf(itemId)));

        verify(itemService, times(2)).get(any(), any());
        verify(bookingService, times(1)).getItemLastBookingMapping(any());
        verify(bookingService, times(1)).getItemNextBookingMapping(any());
    }

    private MockHttpServletResponse performGetItem(Long userId, Long itemId) throws Exception {
        MvcResult mvcResult = mvc.perform(get("/items/{itemId}", itemId)
                        .header(Headers.HEADER_USER_ID, userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        return mvcResult.getResponse();
    }

    @Test
    void getAllUserItems() throws Exception {
        final long userId = user.getId();
        when(itemService.getAll(any(), anyInt(), anyInt())).thenReturn(List.of(item));

        assertThat(performGetAllUserItems(userId, 0, 11).getStatus(), is(200));
        verify(itemService, times(1)).getAll(userId, 0, 11);
    }

    private MockHttpServletResponse performGetAllUserItems(Long userId) throws Exception {
        MvcResult mvcResult = mvc.perform(get("/items")
                        .header(Headers.HEADER_USER_ID, userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        return mvcResult.getResponse();
    }

    private MockHttpServletResponse performGetAllUserItems(Long userId, Integer from, Integer size) throws Exception {
        MvcResult mvcResult = mvc.perform(get("/items")
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
    void updateItem() throws Exception {
        final long userId = user.getId();
        final long itemId = item.getId();
        when(itemService.update(any(), any(), any())).thenReturn(item);

        ItemDto itemDto = ItemDto.builder().name("Name").description("Desc").available(true).build();
        assertThat(performUpdateItem(userId, itemId, itemDto).getStatus(), is(200));
        UpdateItemArgs args = itemMapper.toUpdateItemArgs(itemDto);
        verify(itemService, times(1)).update(args, itemId, userId);

        itemDto = ItemDto.builder().name(null).description(null).available(false).build();
        assertThat(performUpdateItem(userId, itemId, itemDto).getStatus(), is(200));
        args = itemMapper.toUpdateItemArgs(itemDto);
        verify(itemService, times(1)).update(args, itemId, userId);

        assertThat(performUpdateItem(userId, itemId, null).getStatus(), is(400));
    }

    private MockHttpServletResponse performUpdateItem(Long userId, Long itemId, ItemDto itemDto) throws Exception {
        MvcResult mvcResult = mvc.perform(patch("/items/{itemId}", itemId)
                        .header(Headers.HEADER_USER_ID, userId)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        return mvcResult.getResponse();
    }

    @Test
    void searchAvailableItems() throws Exception {
        when(itemService.searchAvailableItems(any(), anyInt(), anyInt())).thenReturn(List.of(item));

        assertThat(performSearchAvailableItems("text", 0, 11).getStatus(), is(200));
        verify(itemService, times(1)).searchAvailableItems("text", 0, 11);
    }

    private MockHttpServletResponse performSearchAvailableItems(String text) throws Exception {
        MvcResult mvcResult = mvc.perform(get("/items/search")
                        .param("text", text)
                        .header(Headers.HEADER_USER_ID, user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        return mvcResult.getResponse();
    }

    private MockHttpServletResponse performSearchAvailableItems(String text, Integer from, Integer size) throws Exception {
        MvcResult mvcResult = mvc.perform(get("/items/search")
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .param("text", text)
                        .header(Headers.HEADER_USER_ID, user.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        return mvcResult.getResponse();
    }

    @Test
    void createComment() throws Exception {
        final long userId = user.getId();
        final long itemId = item.getId();

        CommentDto commentDto = CommentDto.builder().text("text").build();
        assertThat(performCreateComment(userId, itemId, commentDto).getStatus(), is(200));
        verify(itemService, times(1)).createComment(itemMapper.toCreateCommentArgs(commentDto, userId, itemId));
    }

    private MockHttpServletResponse performCreateComment(Long userId, Long itemId, CommentDto commentDto) throws Exception {
        MvcResult mvcResult = mvc.perform(post("/items/{itemId}/comment", itemId)
                        .content(mapper.writeValueAsString(commentDto))
                        .header(Headers.HEADER_USER_ID, userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        return mvcResult.getResponse();
    }
}