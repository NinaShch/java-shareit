package ru.practicum.shareit.item;

import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {
    ItemDto getItemInfo(Long itemId, Long userId);

    ItemDto addNewItem(ItemDto itemDto, Long userId);

    ItemDto changeItem(Long itemId, Long userId, ItemDto itemDto);

    Collection<ItemDto> getItemsByUserId(Long userId, Integer from, Integer size);

    Collection<ItemDto> getItemsByKeyword(String text, Integer from, Integer size);

    CommentDto postComment(Long itemId, Long authorId, CommentDto commentDto);
}
