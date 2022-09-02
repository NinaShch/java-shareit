package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.item.comment.dto.CommentDto;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private List<CommentDto> comments;
    private ExtremumBookingDto lastBooking;
    private ExtremumBookingDto nextBooking;

    @Data
    @AllArgsConstructor
    public static class ExtremumBookingDto {
        @NotNull
        private Long id;
        private Long bookerId;
    }
}
