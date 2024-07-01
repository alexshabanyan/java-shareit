package ru.practicum.shareit.user.controller;

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
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserMapper;
import ru.practicum.shareit.user.model.UserMapperImpl;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(controllers = UserController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import(UserMapperImpl.class)
class UserControllerTest {
    private final MockMvc mvc;
    private final ObjectMapper mapper;

    @MockBean
    private UserService userService;

    private static UserMapper userMapper;
    private User user;

    @BeforeAll
    static void beforeAll() {
        userMapper = new UserMapperImpl();
    }

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("Name");
        user.setEmail("user@mail.com");
    }

    @Test
    void createUser() throws Exception {
        when(userService.create(any())).thenReturn(user);

        UserDto userDto = UserDto.builder().name(user.getName()).email(user.getEmail()).build();
        assertThat(performCreateUser(userDto).getStatus(), is(200));

        verify(userService, times(1)).create(any());
    }

    private MockHttpServletResponse performCreateUser(UserDto userDto) throws Exception {
        MvcResult mvcResult = mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        return mvcResult.getResponse();
    }

    @Test
    void getUser() throws Exception {
        final long userId = user.getId();
        when(userService.get(any())).thenReturn(user);

        MockHttpServletResponse response = performGetUser(userId);
        assertThat(response.getStatus(), is(200));
        assertThat(JsonPath.parse(response.getContentAsString()).read("$.id").toString(), is(String.valueOf(userId)));
        verify(userService, times(1)).get(userId);
    }

    private MockHttpServletResponse performGetUser(Long userId) throws Exception {
        MvcResult mvcResult = mvc.perform(get("/users/{userId}", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        return mvcResult.getResponse();
    }

    @Test
    void getAllUsers() throws Exception {
        final long userId = user.getId();
        when(userService.getAll()).thenReturn(List.of(user));

        MockHttpServletResponse response = performGetAllUsers();
        assertThat(response.getStatus(), is(200));
        assertThat(JsonPath.parse(response.getContentAsString()).read("$[0].id").toString(), is(String.valueOf(userId)));
        verify(userService, times(1)).getAll();
    }

    private MockHttpServletResponse performGetAllUsers() throws Exception {
        MvcResult mvcResult = mvc.perform(get("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        return mvcResult.getResponse();
    }

    @Test
    void updateUser() throws Exception {
        final long userId = user.getId();
        when(userService.update(any(), any())).thenReturn(user);

        UserDto userDto = UserDto.builder().name("updated name").email("updated_email@mail.com").build();
        assertThat(performUpdateUser(userId, userDto).getStatus(), is(200));
        verify(userService, times(1)).update(any(), any());
    }

    private MockHttpServletResponse performUpdateUser(Long userId, UserDto userDto) throws Exception {
        MvcResult mvcResult = mvc.perform(patch("/users/{userId}", userId)
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        return mvcResult.getResponse();
    }

    @Test
    void deleteUser() throws Exception {
        final long userId = user.getId();

        assertThat(performDeleteUser(userId).getStatus(), is(200));
        verify(userService, times(1)).delete(any());
    }

    private MockHttpServletResponse performDeleteUser(Long userId) throws Exception {
        MvcResult mvcResult = mvc.perform(delete("/users/{userId}", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();
        return mvcResult.getResponse();
    }
}