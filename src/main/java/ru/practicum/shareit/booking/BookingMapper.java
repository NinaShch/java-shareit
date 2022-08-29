package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.ExtremumBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;


public class BookingMapper {
    public static Booking toBooking(BookingDto bookingDto, Long id, Item item, User booker, Status status) {
        return new Booking(
                id,
                bookingDto.getStart(),
                bookingDto.getEnd(),
                item,
                booker,
                status
        );
    }

    public static ExtremumBookingDto toExtremumBookingDto(Booking booking) {
        return new ExtremumBookingDto(booking.getId(), booking.getBooker().getId());
    }
}
