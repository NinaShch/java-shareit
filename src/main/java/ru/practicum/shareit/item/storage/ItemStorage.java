package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemStorage {
    ItemDto getItemInfo(Long itemId, Long userId);

    ItemDto add(ItemDto itemDto, Long userId);

    ItemDto change(Long itemId, Long userId, ItemDto itemDto);

    Collection<ItemDto> getById(Long userId);

    Collection<ItemDto> getByKeyWords(String text);
}
