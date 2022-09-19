package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingInputDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingInputDtoTest {

    private static final LocalDateTime START = LocalDateTime.of(2032, 9, 15, 9, 19);
    private static final LocalDateTime END = LocalDateTime.of(2033, 1, 1, 0, 0);

    @Autowired
    private JacksonTester<BookingInputDto> json;

    @Test
    void testBookingInputDto() throws Exception {
        BookingInputDto bookingInputDto = new BookingInputDto(
                1L,
                START,
                END
        );

        JsonContent<BookingInputDto> result = json.write(bookingInputDto);

        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isNotBlank();
        assertThat(result).extractingJsonPathStringValue("$.end").isNotBlank();
    }
}