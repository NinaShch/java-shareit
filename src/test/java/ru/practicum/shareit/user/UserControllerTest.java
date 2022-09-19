package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserControllerTest {
    @Mock
    private UserService service;

    @InjectMocks
    private UserController controller;

    private final ObjectMapper mapper = new ObjectMapper();

    private MockMvc mvc;

    private UserDto dto;

    @BeforeEach
    void before() {
        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();

        dto = new UserDto(
                1L,
                "John Doe",
                "john.doe@mail.com"
        );
    }

    @Test
    void createUser() throws Exception {
        when(service.addUser(any())).thenReturn(dto);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(dto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(dto.getName())))
                .andExpect(jsonPath("$.email", is(dto.getEmail())));

        verify(service).addUser(eq(dto));
    }

    @Test
    void updateUser() throws Exception {
        when(service.updateUser(any(), anyLong())).thenReturn(dto);

        long userId = 123L;
        mvc.perform(patch("/users/" + userId)
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(dto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(dto.getName())))
                .andExpect(jsonPath("$.email", is(dto.getEmail())));

        verify(service).updateUser(eq(dto), eq(userId));
    }

    @Test
    void getAllUsers() throws Exception {
        List<UserDto> userDtos = new ArrayList<>();
        userDtos.add(dto);
        when(service.getAllUsers()).thenReturn(userDtos);

        mvc.perform(get("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(service).getAllUsers();
    }

    @Test
    void getUserById() throws Exception {
        long userId = 123L;
        when(service.getUserById(userId)).thenReturn(dto);

        mvc.perform(get("/users/" + userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(dto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(dto.getName())))
                .andExpect(jsonPath("$.email", is(dto.getEmail())));

        verify(service).getUserById(eq(userId));
    }

    @Test
    void deleteUserById() throws Exception {
        long userId = 123L;

        mvc.perform(delete("/users/" + userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(service).deleteUserById(eq(userId));
    }
}