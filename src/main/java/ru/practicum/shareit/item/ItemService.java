package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    public ItemDto getItemInfo(Long itemId, Long userId) {
        return itemStorage.getItemInfo(itemId, userId);
    }

    public ItemDto addNewItem(ItemDto itemDto, Long userId) {
        if (userStorage.getById(userId).isEmpty()) throw new UserNotFoundException("User not found");
        return itemStorage.add(itemDto, userId);
    }

    public ItemDto changeItem(Long itemId, Long userId, ItemDto itemDto) {
        if (userStorage.getById(userId).isEmpty()) throw new UserNotFoundException("User not found");
        return itemStorage.change(itemId, userId, itemDto);
    }

    public Collection<ItemDto> getItemsByUserId(Long userId) {
        return itemStorage.getById(userId);
    }

    public Collection<ItemDto> getItemsByKeyword(String text) {
        return itemStorage.getByKeyWords(text);
    }
}
