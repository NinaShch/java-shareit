package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemStorage {
    Item getItemInfo(Long itemId);

    Item add(Item item);

    Item change(Long itemId, Long userId, Item itemUpdate);

    Collection<Item> getById(Long userId);

    Collection<Item> getByKeyWords(String text);
}
