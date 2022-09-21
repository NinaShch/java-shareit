package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.practicum.shareit.TestUtil;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.paging.OffsetLimitPageable;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertEquals;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class BookingServiceTest {

    private static final long ITEM_ID = 111L;
    private static final long BOOKING_ID = 222L;
    private static final long BOOKER_ID = 123L;
    private static final long OWNER_ID = 321L;
    private static final long TRESPASSER_ID = 666L;

    private static final LocalDateTime START = LocalDateTime.of(2032, 9, 15, 9, 19);
    private static final LocalDateTime END = LocalDateTime.of(2033, 1, 1, 0, 0);

    @InjectMocks
    private BookingServiceImpl underTest;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Item item;
    @Mock
    private User booker;
    @Mock
    private User owner;
    @Mock
    private User trespasser;
    @Mock
    private Booking booking;

    private final BookingInputDto input = new BookingInputDto(ITEM_ID, START, END);

    @BeforeEach
    public void before() {
        when(bookingRepository.save(any())).thenAnswer(input -> input.getArguments()[0]);
        when(itemRepository.getItemOwner(eq(ITEM_ID))).thenReturn(owner);

        TestUtil.setupItemWithRepo(item, ITEM_ID, "item", owner, itemRepository);

        TestUtil.setupUserWithRepo(booker, BOOKER_ID, "booker", userRepository);
        TestUtil.setupUserWithRepo(owner, OWNER_ID, "owner", userRepository);
        TestUtil.setupUserWithRepo(trespasser, TRESPASSER_ID, "trespasser", userRepository);

        TestUtil.setupBookingWithRepo(booking, BOOKING_ID, START, END, item, booker, Status.WAITING, bookingRepository);
    }

    @Test
    public void throw_not_found_when_no_item_on_add() {
        assertThrows(
                NotFoundException.class,
                () -> {
                    when(itemRepository.findById(eq(ITEM_ID))).thenReturn(Optional.empty());
                    underTest.addNewBooking(input, BOOKER_ID);
                }
        );
    }

    @Test
    public void throw_bad_request_when_item_not_available_on_add() {
        assertThrows(
                BadRequestException.class,
                () -> {
                    when(item.getAvailable()).thenReturn(false);
                    underTest.addNewBooking(input, BOOKER_ID);
                }
        );
    }

    @Test
    public void throw_not_found_when_trying_to_book_own_item() {
        assertThrows(
                NotFoundException.class,
                () -> {
                    when(item.getOwner()).thenReturn(booker);
                    when(itemRepository.getItemOwner(eq(ITEM_ID))).thenReturn(booker);
                    underTest.addNewBooking(input, BOOKER_ID);
                }
        );
    }

    @Test
    public void throw_bad_request_when_start_in_the_past_on_add() {
        assertThrows(
                BadRequestException.class,
                () -> {
                    input.setStart(LocalDateTime.of(1941, 6, 22, 4, 0));
                    underTest.addNewBooking(input, BOOKER_ID);
                }
        );
    }

    @Test
    public void throw_bad_request_when_end_in_the_past_on_add() {
        assertThrows(
                BadRequestException.class,
                () -> {
                    input.setEnd(LocalDateTime.of(1945, 5, 9, 12, 0));
                    underTest.addNewBooking(input, BOOKER_ID);
                }
        );
    }

    @Test
    public void throw_bad_request_when_end_before_start_on_add() {
        assertThrows(
                BadRequestException.class,
                () -> {
                    input.setStart(END);
                    input.setEnd(START);
                    underTest.addNewBooking(input, BOOKER_ID);
                }
        );
    }

    @Test
    public void throw_not_found_when_no_such_user_on_add() {
        assertThrows(
                NotFoundException.class,
                () -> {
                    when(userRepository.findById(eq(BOOKER_ID))).thenReturn(Optional.empty());
                    underTest.addNewBooking(input, BOOKER_ID);
                }
        );
    }

    @Test
    public void saved_to_repo_when_added() {
        underTest.addNewBooking(input, BOOKER_ID);

        ArgumentCaptor<Booking> captor = ArgumentCaptor.forClass(Booking.class);
        verify(bookingRepository).save(captor.capture());

        Booking savedBooking = captor.getValue();
        assertEquals("bookers not match", booker, savedBooking.getBooker());
        assertEquals("items not match", item, savedBooking.getItem());
        assertEquals("starts not match", input.getStart(), savedBooking.getStart());
        assertEquals("ends not match", input.getEnd(), savedBooking.getEnd());
    }

    @Test
    public void waiting_when_added() {
        underTest.addNewBooking(input, BOOKER_ID);

        ArgumentCaptor<Booking> captor = ArgumentCaptor.forClass(Booking.class);
        verify(bookingRepository).save(captor.capture());

        Booking savedBooking = captor.getValue();
        assertEquals("wrong status", Status.WAITING, savedBooking.getStatus());
    }

    @Test
    public void not_found_when_repository_exception_on_add() {
        assertThrows(
                NotFoundException.class,
                () -> {
                    when(bookingRepository.save(any())).thenThrow(new RuntimeException());
                    underTest.addNewBooking(input, BOOKER_ID);
                }
        );
    }

    @Test
    public void returned_correctly_when_added() {
        Booking result = underTest.addNewBooking(input, BOOKER_ID);

        assertEquals("bookers not match", booker, result.getBooker());
        assertEquals("items not match", item, result.getItem());
        assertEquals("starts not match", input.getStart(), result.getStart());
        assertEquals("ends not match", input.getEnd(), result.getEnd());
    }

    @Test
    public void throw_not_found_when_no_such_user_on_approve() {
        assertThrows(
                NotFoundException.class,
                () -> {
                    when(userRepository.findById(eq(OWNER_ID))).thenReturn(Optional.empty());
                    underTest.setBookingApproveStatus(BOOKING_ID, OWNER_ID, true);
                }
        );
    }

    @Test
    public void throw_bad_request_when_already_approved() {
        assertThrows(
                BadRequestException.class,
                () -> {
                    when(booking.getStatus()).thenReturn(Status.APPROVED);
                    underTest.setBookingApproveStatus(BOOKING_ID, OWNER_ID, true);
                }
        );
    }

    @Test
    public void throw_bad_request_when_already_rejected() {
        when(booking.getStatus()).thenReturn(Status.REJECTED);
        assertThrows(
                BadRequestException.class,
                () -> underTest.setBookingApproveStatus(BOOKING_ID, OWNER_ID, true)
        );
    }

    @Test
    public void throw_not_found_when_trying_to_approve_by_booker() {
        assertThrows(
                NotFoundException.class,
                () -> underTest.setBookingApproveStatus(BOOKING_ID, BOOKER_ID, true)
        );
    }

    @Test
    public void throw_not_found_when_trying_to_approve_by_trespasser() {
        assertThrows(
                NotFoundException.class,
                () -> underTest.setBookingApproveStatus(BOOKING_ID, TRESPASSER_ID, true)
        );
    }

    @Test
    public void throw_not_found_when_trying_to_reject_by_trespasser() {
        assertThrows(
                NotFoundException.class,
                () -> underTest.setBookingApproveStatus(BOOKING_ID, TRESPASSER_ID, false)
        );
    }

    @Test
    public void set_approved_status_to_booking_when_approved() {
        underTest.setBookingApproveStatus(BOOKING_ID, OWNER_ID, true);
        verify(booking).setStatus(eq(Status.APPROVED));
    }

    @Test
    public void set_rejected_status_to_booking_when_rejected() {
        underTest.setBookingApproveStatus(BOOKING_ID, OWNER_ID, false);
        verify(booking).setStatus(eq(Status.REJECTED));
    }

    @Test
    public void saved_to_repo_when_approved() {
        underTest.setBookingApproveStatus(BOOKING_ID, OWNER_ID, true);
        verify(booking).setStatus(eq(Status.APPROVED));
    }

    @Test
    public void get_booking_info_returns_booking_for_owner() {
        Booking result = underTest.getBookingInfo(BOOKING_ID, OWNER_ID);
        assertEquals("not same booking", booking, result);
    }

    @Test
    public void get_booking_info_returns_booking_for_booker() {
        Booking result = underTest.getBookingInfo(BOOKING_ID, BOOKER_ID);
        assertEquals("not same booking", booking, result);
    }

    @Test
    public void throws_when_booking_info_requested_by_trespasser() {
        assertThrows(
                NotFoundException.class,
                () -> underTest.getBookingInfo(BOOKING_ID, TRESPASSER_ID)
        );
    }

    @Test
    public void get_all_bookings_asks_repo() {
        underTest.getAllBookings(BOOKER_ID, BookingState.ALL, null, null);
        verify(bookingRepository).findByBooker(eq(booker), any());
    }

    @Test
    public void get_all_bookings_asks_repo_without_paging() {
        underTest.getAllBookings(BOOKER_ID, BookingState.ALL, null, null);
        ArgumentCaptor<OffsetLimitPageable> captor = ArgumentCaptor.forClass(OffsetLimitPageable.class);
        verify(bookingRepository).findByBooker(eq(booker), captor.capture());
        OffsetLimitPageable pageable = captor.getValue();
        assertEquals("wrong offset", 0L, pageable.getOffset());
        assertEquals("wrong page size", Integer.MAX_VALUE, pageable.getPageSize());
    }

    @Test
    public void get_all_bookings_asks_repo_with_paging() {
        underTest.getAllBookings(BOOKER_ID, BookingState.ALL, 2, 20);
        ArgumentCaptor<OffsetLimitPageable> captor = ArgumentCaptor.forClass(OffsetLimitPageable.class);
        verify(bookingRepository).findByBooker(eq(booker), captor.capture());
        OffsetLimitPageable pageable = captor.getValue();
        assertEquals("wrong offset", 2L, pageable.getOffset());
        assertEquals("wrong page size", 20, pageable.getPageSize());
    }

    @Test
    public void get_current_bookings_asks_repo() {
        underTest.getAllBookings(BOOKER_ID, BookingState.CURRENT, null, null);
        verify(bookingRepository).findCurrentByBooker(eq(booker), any());
    }

    @Test
    public void get_past_bookings_asks_repo() {
        underTest.getAllBookings(BOOKER_ID, BookingState.PAST, null, null);
        verify(bookingRepository).findByBookerAndEndIsBefore(eq(booker), any(), any());
    }

    @Test
    public void get_future_bookings_asks_repo() {
        underTest.getAllBookings(BOOKER_ID, BookingState.FUTURE, null, null);
        verify(bookingRepository).findByBookerAndStartIsAfter(eq(booker), any(), any());
    }

    @Test
    public void get_waiting_bookings_asks_repo() {
        underTest.getAllBookings(BOOKER_ID, BookingState.WAITING, null, null);
        verify(bookingRepository).findByBookerAndStatus(eq(booker), eq(Status.WAITING), any());
    }

    @Test
    public void get_rejected_bookings_asks_repo() {
        underTest.getAllBookings(BOOKER_ID, BookingState.REJECTED, null, null);
        verify(bookingRepository).findByBookerAndStatus(eq(booker), eq(Status.REJECTED), any());
    }

    @Test
    public void get_all_bookings_for_owner_asks_repo() {
        underTest.getAllBookingsForOwner(owner.getId(), BookingState.ALL, null, null);
        verify(bookingRepository).findByOwner(eq(owner), any());
    }

    @Test
    public void get_all_bookings_for_owner_asks_repo_without_paging() {
        underTest.getAllBookingsForOwner(owner.getId(), BookingState.ALL, null, null);
        ArgumentCaptor<OffsetLimitPageable> captor = ArgumentCaptor.forClass(OffsetLimitPageable.class);
        verify(bookingRepository).findByOwner(eq(owner), captor.capture());
        OffsetLimitPageable pageable = captor.getValue();
        assertEquals("wrong offset", 0L, pageable.getOffset());
        assertEquals("wrong page size", Integer.MAX_VALUE, pageable.getPageSize());
    }

    @Test
    public void get_all_bookings_for_owner_asks_repo_with_paging() {
        underTest.getAllBookingsForOwner(owner.getId(), BookingState.ALL, 3, 30);
        ArgumentCaptor<OffsetLimitPageable> captor = ArgumentCaptor.forClass(OffsetLimitPageable.class);
        verify(bookingRepository).findByOwner(eq(owner), captor.capture());
        OffsetLimitPageable pageable = captor.getValue();
        assertEquals("wrong offset", 3L, pageable.getOffset());
        assertEquals("wrong page size", 30, pageable.getPageSize());
    }

    @Test
    public void get_current_bookings_for_owner_asks_repo() {
        underTest.getAllBookingsForOwner(owner.getId(), BookingState.CURRENT, null, null);
        verify(bookingRepository).findCurrentByOwner(eq(owner), any());
    }

    @Test
    public void get_past_bookings_for_owner_asks_repo() {
        underTest.getAllBookingsForOwner(owner.getId(), BookingState.PAST, null, null);
        verify(bookingRepository).findByOwnerAndEndIsBefore(eq(owner), any(), any());
    }

    @Test
    public void get_future_bookings_for_owner_asks_repo() {
        underTest.getAllBookingsForOwner(owner.getId(), BookingState.FUTURE, null, null);
        verify(bookingRepository).findByOwnerAndStartIsAfter(eq(owner), any(), any());
    }

    @Test
    public void get_waiting_bookings_for_owner_asks_repo() {
        underTest.getAllBookingsForOwner(owner.getId(), BookingState.WAITING, null, null);
        verify(bookingRepository).findByOwnerAndStatus(eq(owner), eq(Status.WAITING), any());
    }

    @Test
    public void get_rejected_bookings_for_owner_asks_repo() {
        underTest.getAllBookingsForOwner(owner.getId(), BookingState.REJECTED, null, null);
        verify(bookingRepository).findByOwnerAndStatus(eq(owner), eq(Status.REJECTED), any());
    }
}
