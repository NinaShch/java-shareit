package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemDtoTest {

    @Autowired
    private JacksonTester<ItemDto> json;

    @Test
    void testItemDto() throws Exception {
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

        JsonContent<ItemDto> result = json.write(itemDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("My item");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("My item Description");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.comments[0].id").isEqualTo(11);
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.id").isEqualTo(111);
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.id").isEqualTo(222);
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(11111);
    }
}