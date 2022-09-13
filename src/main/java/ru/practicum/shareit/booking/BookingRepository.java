package ru.practicum.shareit.booking;

import org.hibernate.annotations.SortComparator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @SortComparator(BookingDateComparator.class)
    List<Booking> findByBooker(User booker);

    @SortComparator(BookingDateComparator.class)
    List<Booking> findByBookerAndEndIsBefore(User booker, LocalDateTime end);

    @SortComparator(BookingDateComparator.class)
    List<Booking> findByBookerAndStartIsAfter(User booker, LocalDateTime start);

    @SortComparator(BookingDateComparator.class)
    @Query("select b from Booking b where b.booker = :booker and b.start <= current_timestamp and b.end >= current_timestamp")
    List<Booking> findCurrentByBooker(User booker);

    @SortComparator(BookingDateComparator.class)
    List<Booking> findByBookerAndStatus(User booker, Status status);

    @SortComparator(BookingDateComparator.class)
    List<Booking> findByBookerAndItem(User booker, Item item);

    @SortComparator(BookingDateComparator.class)
    List<Booking> findByItem(Item item);

    @SortComparator(BookingDateComparator.class)
    @Query("select b from Booking b where b.item = :item and b.start <= current_timestamp and b.end >= current_timestamp")
    List<Booking> findCurrentByItem(Item item);

    @SortComparator(BookingDateComparator.class)
    List<Booking> findByItemAndEndIsBefore(Item item, LocalDateTime end);

    @SortComparator(BookingDateComparator.class)
    List<Booking> findByItemAndStartIsAfter(Item item, LocalDateTime start);

    @SortComparator(BookingDateComparator.class)
    List<Booking> findByItemAndStatus(Item item, Status status);
}
