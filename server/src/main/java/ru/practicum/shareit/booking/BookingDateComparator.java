package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.Comparator;

class BookingDateComparator implements Comparator<Booking> {

    @Override
    public int compare(Booking o1, Booking o2) {
        LocalDateTime start1 = o1.getStart();
        LocalDateTime start2 = o2.getStart();

        if (start1.isEqual(start2)) return 0;
        else if (start1.isBefore(start2)) return -1;
        else return 1;
    }
}
