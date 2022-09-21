package ru.practicum.shareit.booking;

import java.util.Optional;

enum BookingState {
    ALL, CURRENT, PAST, FUTURE, WAITING, REJECTED;


    public static Optional<BookingState> optionalValueOf(String string) {
        for (BookingState state : BookingState.values()) {
            if (state.name().equals(string)) {
                return Optional.of(state);
            }
        }
        return Optional.empty();
    }
}
