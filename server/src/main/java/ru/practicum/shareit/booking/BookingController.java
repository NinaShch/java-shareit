package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.BadRequestException;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingOutputDto createBooking(
            @RequestBody BookingInputDto bookingInputDto,
            @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        log.info("Request to add booking {}", bookingInputDto);
        return BookingMapper.toBookingOutputDto(bookingService.addNewBooking(bookingInputDto, userId));
    }

    @PatchMapping("/{bookingId}")
    public BookingOutputDto approve(
            @PathVariable Long bookingId,
            @RequestParam boolean approved,
            @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        log.info("Request to approve booking id = {}, approved: {}", bookingId, approved);
        return BookingMapper.toBookingOutputDto(bookingService.setBookingApproveStatus(bookingId, userId, approved));
    }

    @GetMapping("/{bookingId}")
    public BookingOutputDto getBookingInfo(@PathVariable Long bookingId,
                                           @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Request booking info by id, id = {}", bookingId);
        return BookingMapper.toBookingOutputDto(bookingService.getBookingInfo(bookingId, userId));
    }

    @GetMapping
    public List<BookingOutputDto> getAllBookings(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "ALL", required = false) String state,
            @RequestParam(required = false) Integer from,
            @RequestParam(required = false) Integer size
    ) {
        log.info("Request all bookings, userId = {}, state = {}", userId, state);
        BookingState bookingState = BookingState.optionalValueOf(state).orElseThrow(
                () -> new BadRequestException("Unknown state: UNSUPPORTED_STATUS"));
        List<Booking> bookings = bookingService.getAllBookings(userId, bookingState, from, size);
        return BookingMapper.toBookingOutputDtoList(bookings);
    }

    @GetMapping("owner")
    public List<BookingOutputDto> getAllBookingsForOwner(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "ALL", required = false) String state,
            @RequestParam(required = false) Integer from,
            @RequestParam(required = false) Integer size
    ) {
        log.info("Request all bookings for owner, userId = {}, state = {}", userId, state);
        try {
            BookingState bookingState = state != null ? BookingState.valueOf(state) : BookingState.ALL;
            List<Booking> bookings = bookingService.getAllBookingsForOwner(userId, bookingState, from, size);
            return BookingMapper.toBookingOutputDtoList(bookings);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Unknown state: UNSUPPORTED_STATUS");
        }
    }
}
