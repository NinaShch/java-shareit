package ru.practicum.shareit.booking;

import org.hibernate.annotations.SortComparator;
import org.springframework.data.domain.Pageable;
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
    List<Booking> findByBooker(User booker, Pageable pageable);

    @SortComparator(BookingDateComparator.class)
    List<Booking> findByBookerAndEndIsBefore(User booker, LocalDateTime end, Pageable pageable);

    @SortComparator(BookingDateComparator.class)
    List<Booking> findByBookerAndStartIsAfter(User booker, LocalDateTime start, Pageable pageable);

    @SortComparator(BookingDateComparator.class)
    @Query("select b from Booking b where b.booker = :booker and b.start <= current_timestamp and b.end >= current_timestamp")
    List<Booking> findCurrentByBooker(User booker, Pageable pageable);

    @SortComparator(BookingDateComparator.class)
    List<Booking> findByBookerAndStatus(User booker, Status status, Pageable pageable);

    @SortComparator(BookingDateComparator.class)
    List<Booking> findByBookerAndItem(User booker, Item item);


    @SortComparator(BookingDateComparator.class)
    @Query("select b from Booking b, Item i where b.item = i and i.owner = :owner")
    List<Booking> findByOwner(User owner, Pageable pageable);

    @SortComparator(BookingDateComparator.class)
    @Query("select b from Booking b, Item i where b.item = i and i.owner = :owner and b.end <= :end")
    List<Booking> findByOwnerAndEndIsBefore(User owner, LocalDateTime end, Pageable pageable);

    @SortComparator(BookingDateComparator.class)
    @Query("select b from Booking b, Item i where b.item = i and i.owner = :owner and b.start >= :start")
    List<Booking> findByOwnerAndStartIsAfter(User owner, LocalDateTime start, Pageable pageable);

    @SortComparator(BookingDateComparator.class)
    @Query("select b from Booking b, Item i where b.item = i and i.owner = :owner and b.start <= current_timestamp and b.end >= current_timestamp")
    List<Booking> findCurrentByOwner(User owner, Pageable pageable);

    @SortComparator(BookingDateComparator.class)
    @Query("select b from Booking b, Item i where b.item = i and i.owner = :owner and b.status = :status")
    List<Booking> findByOwnerAndStatus(User owner, Status status, Pageable pageable);

    @SortComparator(BookingDateComparator.class)
    List<Booking> findByItemAndEndIsBefore(Item item, LocalDateTime end);

    @SortComparator(BookingDateComparator.class)
    List<Booking> findByItemAndStartIsAfter(Item item, LocalDateTime start);

    @SortComparator(BookingDateComparator.class)
    @Query("select b from Booking b, Item i where b.item = i and b.end <= current_timestamp")
    List<Booking> findByItemAndEndIsInPast(Item item);

    @SortComparator(BookingDateComparator.class)
    @Query("select b from Booking b, Item i where b.item = i and b.start >= current_timestamp")
    List<Booking> findByItemAndStartIsInFuture(Item item);

}
