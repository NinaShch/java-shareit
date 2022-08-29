package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public Booking addNewBooking(BookingDto bookingDto, Long userId) {
            Item item = itemRepository.findById(bookingDto.getItemId())
                    .orElseThrow(() -> new NotFoundException("item not found"));
            if (!item.getAvailable())
                throw new BadRequestException("item not available");
            if (userId.equals(itemRepository.getItemOwner(item.getId()).getId())) {
                throw new NotFoundException("can't book own item");
            }
            verifyNotInPast(bookingDto.getStart());
            verifyNotInPast(bookingDto.getEnd());
            if (bookingDto.getEnd().isBefore(bookingDto.getStart())) {
                throw new BadRequestException("booking end is before start");
            }
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new NotFoundException("user not found"));
        try {
            Booking booking = BookingMapper.toBooking(bookingDto, null, item, user, Status.WAITING);
            bookingRepository.save(booking);
            return booking;
        } catch (Exception e) {
            throw new NotFoundException(e.getMessage());
        }
    }

    public Booking setBookingApproveStatus(Long bookingId, Long userId, boolean isApproved) {
        User user = getUser(userId);
        Booking booking = getBooking(bookingId);

        if (booking.getStatus() != Status.WAITING) {
            throw new BadRequestException("Booking status can only be changed when waiting for it");
        }

        boolean canSetStatus = booking.getItem().getOwner().equals(user);
        if (!canSetStatus) {
            throw new NotFoundException("Booking status can only be changed by item's owner");
        }

        booking.setStatus(isApproved ? Status.APPROVED : Status.REJECTED);
        bookingRepository.save(booking);

        return booking;
    }

    public Booking getBookingInfo(Long bookingId, Long userId) {
        User user = getUser(userId);
        Booking booking = getBooking(bookingId);

        boolean canGetInfo = booking.getItem().getOwner().equals(user) ||
                booking.getBooker().equals(user);

        if (!canGetInfo) {
            throw new NotFoundException("Booking info can only be accessed by booker or item's owner");
        }

        return booking;
    }

    public List<Booking> getAllBookings(Long bookerId, BookingGetState state) {
        User booker = getUser(bookerId);

        switch (state) {
            case ALL:
                return bookingRepository.findByBooker(booker);
            case CURRENT:
                return bookingRepository.findCurrentByBooker(booker);
            case PAST:
                return bookingRepository.findByBookerAndEndIsBefore(booker, LocalDateTime.now());
            case FUTURE:
                return bookingRepository.findByBookerAndStartIsAfter(booker, LocalDateTime.now());
            case WAITING:
                return bookingRepository.findByBookerAndStatus(booker, Status.WAITING);
            case REJECTED:
                return bookingRepository.findByBookerAndStatus(booker, Status.REJECTED);
        }

        throw new BadRequestException("Unknown state: UNSUPPORTED_STATUS");
    }

    public List<Booking> getAllBookingsForOwner(Long ownerId, BookingGetState state) {
        User owner = getUser(ownerId);
        List<Item> items = itemRepository.findByOwner(owner);

        return items.stream()
                .flatMap((item) -> getAllBookingsForItem(item, state).stream())
                .sorted(new BookingDateComparator())
                .collect(Collectors.toList());
    }

    public List<Booking> getAllBookingsForItem(Item item, BookingGetState state) {
        switch (state) {
            case ALL:
                return bookingRepository.findByItem(item);
            case CURRENT:
                return bookingRepository.findCurrentByItem(item);
            case PAST:
                return bookingRepository.findByItemAndEndIsBefore(item, LocalDateTime.now());
            case FUTURE:
                return bookingRepository.findByItemAndStartIsAfter(item, LocalDateTime.now());
            case WAITING:
                return bookingRepository.findByItemAndStatus(item, Status.WAITING);
            case REJECTED:
                return bookingRepository.findByItemAndStatus(item, Status.REJECTED);
        }

        throw new IllegalArgumentException("Unknown state: UNSUPPORTED_STATUS");
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("user not found"));
    }

    private Booking getBooking(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("booking not found"));
    }

    private void verifyNotInPast(LocalDateTime localDateTime) {
        if (localDateTime.isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Date is in the past");
        }
    }
}
