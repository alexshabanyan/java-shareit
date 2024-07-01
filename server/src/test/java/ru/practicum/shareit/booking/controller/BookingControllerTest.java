package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
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
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingMapperImpl;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.utils.Headers;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@WebMvcTest(controllers = BookingController.class)
@Import(BookingMapperImpl.class)
class BookingControllerTest {
    private final MockMvc mvc;
    private final ObjectMapper mapper;

    @MockBean
    private BookingService bookingService;

    private Booking booking;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setId(1L);
        Item item = new Item();
        item.setId(2L);
        LocalDateTime start = LocalDateTime.now().plusDays(5);
        LocalDateTime end = start.plusDays(2);
        booking = new Booking();
        booking.setId(3L);
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStart(start);
        booking.setEnd(end);
    }

    @Test
    void createBooking() throws Exception {
        final long userId = booking.getBooker().getId();
        final long itemId = booking.getBooker().getId();
        final LocalDateTime start = booking.getStart();
        final LocalDateTime end = booking.getEnd();

        when(bookingService.createBooking(any())).thenReturn(booking);

        assertThat(performCreateBooking(userId, itemId, start, end).getStatus(), is(200));
        verify(bookingService, times(1)).createBooking(any());
    }

    private MockHttpServletResponse performCreateBooking(Long userId, Long itemId, LocalDateTime start, LocalDateTime end) throws Exception {
        CreateBookingDto createBookingDto = CreateBookingDto.builder()
                .itemId(itemId).start(start).end(end).build();
        MvcResult mvcResult = mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(createBookingDto))
                        .header(Headers.HEADER_USER_ID, userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        return mvcResult.getResponse();
    }

    @Test
    void getBooking() throws Exception {
        final long bookingId = booking.getId();
        final long userId = booking.getBooker().getId();

        when(bookingService.getBooking(any(), any())).thenReturn(booking);

        assertThat(performGetBooking(userId, bookingId).getStatus(), is(200));
        verify(bookingService, times(1)).getBooking(userId, bookingId);
    }

    private MockHttpServletResponse performGetBooking(Long userId, Long bookingId) throws Exception {
        MvcResult mvcResult = mvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header(Headers.HEADER_USER_ID, userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        return mvcResult.getResponse();
    }

    @Test
    void getBookingsByState() throws Exception {
        final long userId = booking.getBooker().getId();

        when(bookingService.getBookingsForUser(any(), any(), anyInt(), anyInt())).thenReturn(List.of(booking));

        assertThat(performGetBookingsByState(userId, 0, 11, BookingState.PAST.toString()).getStatus(), is(200));
        verify(bookingService, times(1)).getBookingsForUser(userId, BookingState.PAST, 0, 11);

        assertThat(performGetBookingsByState(userId, 0, 11, null).getStatus(), is(200));
        verify(bookingService, times(1)).getBookingsForUser(userId, BookingState.ALL, 0, 11);
    }

    private MockHttpServletResponse performGetBookingsByState(Long userId, String state) throws Exception {
        MvcResult mvcResult = mvc.perform(get("/bookings")
                        .param("state", state)
                        .header(Headers.HEADER_USER_ID, userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        return mvcResult.getResponse();
    }

    private MockHttpServletResponse performGetBookingsByState(Long userId, Integer from, Integer size,
                                                              String state) throws Exception {
        MvcResult mvcResult = mvc.perform(get("/bookings")
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .param("state", state)
                        .header(Headers.HEADER_USER_ID, userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        return mvcResult.getResponse();
    }


    @Test
    void getBookingsByStateOwner() throws Exception {
        final long userId = booking.getBooker().getId();

        when(bookingService.getBookingsForItemOwner(any(), any(), anyInt(), anyInt())).thenReturn(List.of(booking));

        assertThat(performGetBookingsByStateOwner(userId, 0, 11, BookingState.PAST.toString()).getStatus(), is(200));
        verify(bookingService, times(1)).getBookingsForItemOwner(userId, BookingState.PAST, 0, 11);

        assertThat(performGetBookingsByStateOwner(userId, 0, 11, null).getStatus(), is(200));
        verify(bookingService, times(1)).getBookingsForItemOwner(userId, BookingState.ALL, 0, 11);
    }

    private MockHttpServletResponse performGetBookingsByStateOwner(Long userId, String state) throws Exception {
        MvcResult mvcResult = mvc.perform(get("/bookings/owner")
                        .param("state", state)
                        .header(Headers.HEADER_USER_ID, userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        return mvcResult.getResponse();
    }

    private MockHttpServletResponse performGetBookingsByStateOwner(Long userId, Integer from, Integer size,
                                                                   String state) throws Exception {
        MvcResult mvcResult = mvc.perform(get("/bookings/owner")
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .param("state", state)
                        .header(Headers.HEADER_USER_ID, userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        return mvcResult.getResponse();
    }

    @Test
    void updateBookingStatus() throws Exception {
        final long userId = booking.getBooker().getId();
        final long bookingId = booking.getId();

        when(bookingService.updateBookingStatus(any(), any(), anyBoolean())).thenReturn(booking);

        assertThat(performUpdateBookingStatus(userId, bookingId, true).getStatus(), is(200));
        verify(bookingService, times(1)).updateBookingStatus(userId, bookingId, true);

        assertThat(performUpdateBookingStatus(userId, bookingId, false).getStatus(), is(200));
        verify(bookingService, times(1)).updateBookingStatus(userId, bookingId, false);
    }

    private MockHttpServletResponse performUpdateBookingStatus(Long userId, Long bookingId, Boolean approved) throws Exception {
        MvcResult mvcResult = mvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .param("approved", String.valueOf(approved))
                        .header(Headers.HEADER_USER_ID, userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        return mvcResult.getResponse();
    }
}