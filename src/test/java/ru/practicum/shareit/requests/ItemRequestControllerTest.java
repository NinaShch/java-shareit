package ru.practicum.shareit.requests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.requests.dto.ItemRequestDto;

import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ItemRequestControllerTest {

    @Mock
    private ItemRequestService service;

    @InjectMocks
    private ItemRequestController controller;

    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    private MockMvc mvc;

    private ItemRequestDto dto;

    @BeforeEach
    void before() {
        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();

        dto = new ItemRequestDto(
                1L,
                "Need a Chainsaw",
                ZonedDateTime.now(),
                null
        );
    }

    @Test
    void addNew() throws Exception {
        when(service.addNew(any(), anyLong())).thenReturn(dto);

        long userId = 123L;
        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(dto))
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(dto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(dto.getDescription())));

        ArgumentCaptor<ItemRequestDto> captor = ArgumentCaptor.forClass(ItemRequestDto.class);
        verify(service).addNew(captor.capture(), eq(userId));
        ItemRequestDto actual = captor.getValue();
        assertEquals("wrong id", dto.getId(), actual.getId());
        assertEquals("wrong description", dto.getDescription(), actual.getDescription());
    }

    @Test
    void getByUserId() throws Exception {
        List<ItemRequestDto> dtos = new ArrayList<>();
        dtos.add(dto);
        long userId = 123L;
        when(service.getByUserId(anyLong())).thenReturn(dtos);

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(service).getByUserId(eq(userId));
    }

    @Test
    void getAllWithoutPaging() throws Exception {
        List<ItemRequestDto> dtos = new ArrayList<>();
        dtos.add(dto);
        long userId = 123L;
        when(service.getAll(anyLong(), any(), any())).thenReturn(dtos);

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(service).getAll(eq(userId), isNull(), isNull());
    }

    @Test
    void getAllWithPaging() throws Exception {
        List<ItemRequestDto> dtos = new ArrayList<>();
        dtos.add(dto);
        long userId = 123L;
        when(service.getAll(anyLong(), any(), any())).thenReturn(dtos);

        mvc.perform(get("/requests/all?from=1&size=10")
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(service).getAll(eq(userId), eq(1), eq(10));
    }

    @Test
    void getById() throws Exception {
        when(service.getById(anyLong(), anyLong())).thenReturn(dto);

        long userId = 123L;
        long itemRequestId = 321L;

        mvc.perform(get("/requests/" + itemRequestId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(dto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(dto.getDescription())));

        verify(service).getById(eq(itemRequestId), eq(userId));
    }
}
