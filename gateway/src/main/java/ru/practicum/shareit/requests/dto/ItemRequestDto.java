package ru.practicum.shareit.requests.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.NotBlank;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * The user creates such a request when he cannot find the right thing by using the search,
 * but at the same time hopes that someone still has it. Other users can view similar requests and,
 * if they have the described item, and they are ready to rent it, add the necessary item in response to the request.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestDto {
    private long id;
    @NotBlank
    private String description;
    private ZonedDateTime created;
    private List<ItemDto> items;
}
