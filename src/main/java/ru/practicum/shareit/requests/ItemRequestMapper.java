package ru.practicum.shareit.requests;

import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class ItemRequestMapper {

    public static ItemRequestDto toItemRequestDto(
            ItemRequest itemRequest,
            List<Item> items
   ) {
        return new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated(),
                items.stream().map(item -> ItemMapper.toItemDto(item, null, null, null))
                        .collect(Collectors.toList())
        );
    }

    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto, User requestor) {
        ZonedDateTime created = itemRequestDto.getCreated();
        if (created == null) {
            created = ZonedDateTime.now();
        }

        return new ItemRequest(
                itemRequestDto.getId(),
                itemRequestDto.getDescription(),
                requestor,
                created
        );
    }
}
