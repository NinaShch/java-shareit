package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.paging.OffsetLimitPageable;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public Booking addNewBooking(BookingInputDto bookingInputDto, Long userId) {
        Item item = itemRepository.findById(bookingInputDto.getItemId())
                .orElseThrow(() -> new NotFoundException("item not found"));
        if (!item.getAvailable())
            throw new BadRequestException("item not available");
        if (userId.equals(itemRepository.getItemOwner(item.getId()).getId())) {
            throw new NotFoundException("can't book own item");
        }
        verifyNotInPast(bookingInputDto.getStart());
        verifyNotInPast(bookingInputDto.getEnd());
        if (bookingInputDto.getEnd().isBefore(bookingInputDto.getStart())) {
            throw new BadRequestException("booking end is before start");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("user not found"));
        Booking booking = BookingMapper.toBooking(bookingInputDto, null, item, user, Status.WAITING);
        bookingRepository.save(booking);
        return booking;
    }

    @Override
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

    @Override
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

    @Override
    public List<Booking> getAllBookings(Long bookerId, BookingState state, Integer from, Integer size) {
        User booker = getUser(bookerId);
        Pageable pageable = OffsetLimitPageable.create(from, size, Sort.by(Sort.Direction.DESC, "start"));

        switch (state) {
            case ALL:
                return bookingRepository.findByBooker(booker, pageable);
            case CURRENT:
                return bookingRepository.findCurrentByBooker(booker, pageable);
            case PAST:
                return bookingRepository.findByBookerAndEndIsBefore(booker, LocalDateTime.now(), pageable);
            case FUTURE:
                return bookingRepository.findByBookerAndStartIsAfter(booker, LocalDateTime.now(), pageable);
            case WAITING:
                return bookingRepository.findByBookerAndStatus(booker, Status.WAITING, pageable);
            case REJECTED:
                return bookingRepository.findByBookerAndStatus(booker, Status.REJECTED, pageable);
        }

        throw new BadRequestException("Unknown state: UNSUPPORTED_STATUS");
    }

    @Override
    public List<Booking> getAllBookingsForOwner(Long ownerId, BookingState state, Integer from, Integer size) {
        User owner = getUser(ownerId);
        Pageable pageable = OffsetLimitPageable.create(from, size, Sort.by(Sort.Direction.DESC, "start"));

        switch (state) {
            case ALL:
                return bookingRepository.findByOwner(owner, pageable);
            case CURRENT:
                return bookingRepository.findCurrentByOwner(owner, pageable);
            case PAST:
                return bookingRepository.findByOwnerAndEndIsBefore(owner, LocalDateTime.now(), pageable);
            case FUTURE:
                return bookingRepository.findByOwnerAndStartIsAfter(owner, LocalDateTime.now(), pageable);
            case WAITING:
                return bookingRepository.findByOwnerAndStatus(owner, Status.WAITING, pageable);
            case REJECTED:
                return bookingRepository.findByOwnerAndStatus(owner, Status.REJECTED, pageable);
        }

        throw new IllegalArgumentException("Unknown state: UNSUPPORTED_STATUS");
    }

    @Override
    public User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("user not found"));
    }

    @Override
    public Booking getBooking(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("booking not found"));
    }

    @Override
    public void verifyNotInPast(LocalDateTime localDateTime) {
        if (localDateTime.isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Date is in the past");
        }
    }
}
