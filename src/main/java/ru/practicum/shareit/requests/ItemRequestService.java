package ru.practicum.shareit.requests;

import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto addNew(ItemRequestDto itemRequestDto, Long userId);

    List<ItemRequestDto> getByUserId(Long userId);

    List<ItemRequestDto> getAll(Long userId, Integer from, Integer size);

    ItemRequestDto getById(Long itemRequestId, Long userId);

    User getUser(Long userId);
}
