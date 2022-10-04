package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;


public class BookingMapper {
    public static Booking toBooking(BookingInputDto bookingInputDto, Long id, Item item, User booker, Status status) {
        return new Booking(
                id,
                bookingInputDto.getStart(),
                bookingInputDto.getEnd(),
                item,
                booker,
                status
        );
    }

    public static BookingOutputDto toBookingOutputDto(Booking booking) {
        return new BookingOutputDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem(),
                booking.getBooker(),
                booking.getStatus()
        );
    }

    public static List<BookingOutputDto> toBookingOutputDtoList(List<Booking> bookings) {
        return bookings.stream()
                .map(BookingMapper::toBookingOutputDto)
                .collect(Collectors.toList());
    }

    public static ItemDto.ExtremumBookingDto toExtremumBookingDto(Booking booking) {
        return new ItemDto.ExtremumBookingDto(booking.getId(), booking.getBooker().getId());
    }
    // обратный маппинг не нужен так как он в принципе никогда нигде не будет использоваться
    // потому что это не имеет смысла, для этого есть BookingInputDto
}
