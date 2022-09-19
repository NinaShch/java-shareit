package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
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
class ItemControllerTest {

    @Mock
    private ItemService service;

    @InjectMocks
    private ItemController itemController;

    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    private MockMvc mvc;

    private ItemDto itemDto;

    @BeforeEach
    void before() {
        mvc = MockMvcBuilders
                .standaloneSetup(itemController)
                .build();

        itemDto = new ItemDto(
                1L,
                "Chainsaw",
                "bzzzzzzzzzz grrrr",
                true,
                null,
                null,
                null,
                null
        );
    }

    @Test
    void addNewItem() throws Exception {
        when(service.addNewItem(any(), anyLong())).thenReturn(itemDto);

        long userId = 123L;
        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())));

        verify(service).addNewItem(eq(itemDto), eq(userId));
    }

    @Test
    void changeItem() throws Exception {
        when(service.changeItem(anyLong(), anyLong(), any())).thenReturn(itemDto);

        long userId = 123L;
        long itemId = 321L;
        mvc.perform(patch("/items/" + itemId)
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())));

        verify(service).changeItem(eq(itemId), eq(userId), eq(itemDto));
    }

    @Test
    void getItemInfo() throws Exception {
        when(service.getItemInfo(anyLong(), anyLong())).thenReturn(itemDto);

        long userId = 123L;
        long itemId = 321L;

        mvc.perform(get("/items/" + itemId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())));

        verify(service).getItemInfo(eq(itemId), eq(userId));
    }

    @Test
    void getAllItems() throws Exception {
        List<ItemDto> dtos = new ArrayList<>();
        dtos.add(itemDto);
        Long userId = 123L;
        when(service.getItemsByUserId(userId, null, null)).thenReturn(dtos);

        mvc.perform(get("/items")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(service).getItemsByUserId(eq(userId), any(), any());
    }

    @Test
    void getItemsByKeyword() throws Exception {
        List<ItemDto> dtos = new ArrayList<>();
        dtos.add(itemDto);
        String keyword = "chainsaw";
        when(service.getItemsByKeyword(eq(keyword), any(), any())).thenReturn(dtos);

        mvc.perform(get("/items/search?text=" + keyword)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(service).getItemsByKeyword(eq(keyword), any(), any());
    }

    @Test
    void postComment() throws Exception {
        long commentId = 456L;
        LocalDateTime created = LocalDateTime.now();
        CommentDto commentDto = new CommentDto(commentId, "cool item!", "user", created);

        when(service.postComment(anyLong(), anyLong(), any())).thenReturn(commentDto);

        long userId = 123L;
        long itemId = 321L;
        mvc.perform(post("/items/" + itemId + "/comment")
                        .content(mapper.writeValueAsString(commentDto))
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText())))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())));

        verify(service).postComment(eq(itemId), eq(userId), eq(commentDto));
    }
}
