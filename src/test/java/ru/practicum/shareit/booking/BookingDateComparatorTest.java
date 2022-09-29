package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.mockito.Mockito.mock;
import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertTrue;

public class BookingDateComparatorTest {

    private static final LocalDateTime START1 =
            LocalDateTime.of(2032, 9, 15, 9, 19);
    private static final LocalDateTime START2 =
            LocalDateTime.of(2033, 1, 1, 0, 0);

    private BookingDateComparator underTest = new BookingDateComparator();

    @Test
    public void equalBookings() {
        Booking booking1 = new Booking(
                1L,
                START1,
                LocalDateTime.now(),
                mock(Item.class),
                mock(User.class),
                Status.APPROVED
        );
        Booking booking2 = new Booking(
                2L,
                START1,
                LocalDateTime.now(),
                mock(Item.class),
                mock(User.class),
                Status.REJECTED
        );

        assertEquals("bookings not equal", 0, underTest.compare(booking1, booking2));
    }

    @Test
    public void isBefore() {
        Booking booking1 = new Booking(
                1L,
                START1,
                LocalDateTime.now(),
                mock(Item.class),
                mock(User.class),
                Status.APPROVED
        );
        Booking booking2 = new Booking(
                2L,
                START2,
                LocalDateTime.now(),
                mock(Item.class),
                mock(User.class),
                Status.REJECTED
        );

        assertTrue("is after", underTest.compare(booking1, booking2) < 0);
    }

    @Test
    public void isAfter() {
        Booking booking1 = new Booking(
                1L,
                START2,
                LocalDateTime.now(),
                mock(Item.class),
                mock(User.class),
                Status.APPROVED
        );
        Booking booking2 = new Booking(
                2L,
                START1,
                LocalDateTime.now(),
                mock(Item.class),
                mock(User.class),
                Status.REJECTED
        );

        assertTrue("is before", underTest.compare(booking1, booking2) > 0);
    }
}
