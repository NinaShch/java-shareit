package ru.practicum.shareit.requests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.requests.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemRequestDtoTest {

    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @Test
    void testItemRequestDto() throws Exception {
        CommentDto commentDto = new CommentDto(11L, "comment text", "commentor", LocalDateTime.now());
        List<CommentDto> comments = new ArrayList<>();
        comments.add(commentDto);
        ItemDto.ExtremumBookingDto lastBooking = new ItemDto.ExtremumBookingDto(111L, 1111L);
        ItemDto.ExtremumBookingDto nextBooking = new ItemDto.ExtremumBookingDto(222L, 2222L);
        ItemDto itemDto = new ItemDto(
                1L,
                "My item",
                "My item Description",
                true,
                comments,
                lastBooking,
                nextBooking,
                11111L
        );
        List<ItemDto> items = new ArrayList<>();
        items.add(itemDto);

        ItemRequestDto itemRequestDto = new ItemRequestDto(
                123L,
                "My long Description",
                ZonedDateTime.now(),
                items
        );

        JsonContent<ItemRequestDto> result = json.write(itemRequestDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(123);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("My long Description");
        assertThat(result).extractingJsonPathStringValue("$.created").isNotBlank();
        assertThat(result).extractingJsonPathNumberValue("$.items[0].id").isEqualTo(1);
    }
}