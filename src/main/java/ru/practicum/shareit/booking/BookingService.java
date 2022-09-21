package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingService {
    Booking addNewBooking(BookingInputDto bookingInputDto, Long userId);

    Booking setBookingApproveStatus(Long bookingId, Long userId, boolean isApproved);

    Booking getBookingInfo(Long bookingId, Long userId);

    List<Booking> getAllBookings(Long bookerId, BookingState state, Integer from, Integer size);

    List<Booking> getAllBookingsForOwner(Long ownerId, BookingState state, Integer from, Integer size);

    User getUser(Long userId);

    Booking getBooking(Long bookingId);

    void verifyNotInPast(LocalDateTime localDateTime);
}
