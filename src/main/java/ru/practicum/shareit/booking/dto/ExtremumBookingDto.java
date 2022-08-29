package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class ExtremumBookingDto {
    @NotNull
    private Long id;
    private Long bookerId;
}
