package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.BadRequestException;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public Booking createBooking(
            @Valid @RequestBody BookingDto bookingDto,
            @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        log.info("Request to add booking {}", bookingDto);
        return bookingService.addNewBooking(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public Booking approve(
            @PathVariable Long bookingId,
            @RequestParam boolean approved,
            @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        log.info("Request to approve booking id = {}, approved: {}", bookingId, approved);
        return bookingService.setBookingApproveStatus(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public Booking getBookingInfo(@PathVariable Long bookingId, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Request booking info by id, id = {}", bookingId);
        return bookingService.getBookingInfo(bookingId, userId);
    }

    @GetMapping
    public List<Booking> getAllBookings(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "ALL", required = false) String state
    ) {
        log.info("Request all bookings, userId = {}, state = {}", userId, state);
        try {
            BookingGetState bookingGetState = state != null ? BookingGetState.valueOf(state) : BookingGetState.ALL;
            return bookingService.getAllBookings(userId, bookingGetState).stream()
                    .sorted(new BookingDateComparator().reversed())
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    @GetMapping("owner")
    public List<Booking> getAllBookingsForOwner(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "ALL", required = false) String state
    ) {
        log.info("Request all bookings for owner, userId = {}, state = {}", userId, state);
        try {
            BookingGetState bookingGetState = state != null ? BookingGetState.valueOf(state) : BookingGetState.ALL;
            return bookingService.getAllBookingsForOwner(userId, bookingGetState).stream()
                    .sorted(new BookingDateComparator().reversed())
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Unknown state: UNSUPPORTED_STATUS");
        }
    }
}
