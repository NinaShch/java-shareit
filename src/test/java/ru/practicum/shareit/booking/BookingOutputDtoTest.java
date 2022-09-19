package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingOutputDtoTest {

    @Autowired
    private JacksonTester<BookingOutputDto> json;

    private static final LocalDateTime START = LocalDateTime.of(2032, 9, 15, 9, 19);
    private static final LocalDateTime END = LocalDateTime.of(2033, 1, 1, 0, 0);

    @Test
    void testBookingOutputDto() throws Exception {
        BookingOutputDto bookingOutputDto = new BookingOutputDto(
                1L,
                START,
                END,
                createItem(),
                createUser(),
                Status.APPROVED
        );

        JsonContent<BookingOutputDto> result = json.write(bookingOutputDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isNotBlank();
        assertThat(result).extractingJsonPathStringValue("$.end").isNotBlank();
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(11);
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(111);
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("APPROVED");
    }

    private Item createItem() {
        return new Item(
                11L,
                "My item",
                "My item Description",
                true,
                createUser(),
                null
        );
    }

    private User createUser() {
        return new User(
                111L,
                "John Doe",
                "john.doe@mail.com"
        );
    }
}