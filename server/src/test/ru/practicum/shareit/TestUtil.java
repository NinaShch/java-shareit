package ru.practicum.shareit;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.storage.ItemRequestRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

public class TestUtil {

    public static void setupUser(User user, long id, String name) {
        when(user.getId()).thenReturn(id);
        when(user.getName()).thenReturn(name);
        when(user.getEmail()).thenReturn(name + "@yandex.ru");
    }

    public static void setupUserWithRepo(User user, long id, String name, UserRepository repo) {
        setupFindById(repo, user, id);
        setupUser(user, id, name);
    }

    public static void setupItem(Item item, long id, String name, User owner) {
        when(item.getId()).thenReturn(id);
        when(item.getName()).thenReturn(name);
        when(item.getDescription()).thenReturn(name + " " + id);
        when(item.getAvailable()).thenReturn(true);
        when(item.getOwner()).thenReturn(owner);
    }

    public static void setupItemWithRepo(Item item, long id, String name, User owner, ItemRepository repo) {
        setupFindById(repo, item, id);
        setupItem(item, id, name, owner);
    }

    public static void setupBooking(
            Booking booking,
            long id,
            LocalDateTime start,
            LocalDateTime end,
            Item item,
            User booker,
            Status status
    ) {
        when(booking.getId()).thenReturn(id);
        when(booking.getStart()).thenReturn(start);
        when(booking.getEnd()).thenReturn(end);
        when(booking.getItem()).thenReturn(item);
        when(booking.getBooker()).thenReturn(booker);
        when(booking.getStatus()).thenReturn(Status.WAITING);
    }

    public static void setupBookingWithRepo(
            Booking booking,
            long id,
            LocalDateTime start,
            LocalDateTime end,
            Item item,
            User booker,
            Status status,
            BookingRepository repo
    ) {
        setupFindById(repo, booking, id);
        setupBooking(booking, id, start, end, item, booker, status);
    }

    public static void setupItemRequest(
            ItemRequest request,
            long id,
            User requestor
    ) {
        when(request.getId()).thenReturn(id);
        when(request.getDescription()).thenReturn("Item request #" + id);
        when(request.getRequestor()).thenReturn(requestor);
    }

    public static void setupItemRequestWithRepo(
            ItemRequest request,
            long id,
            User requestor,
            ItemRequestRepository repo
    ) {
        setupFindById(repo, request, id);
        setupItemRequest(request, id, requestor);
    }

    private static <M> void setupFindById(JpaRepository<M, Long> repo, M mock, long id) {
        when(repo.findById(eq(id))).thenReturn(Optional.of(mock));
    }
}
