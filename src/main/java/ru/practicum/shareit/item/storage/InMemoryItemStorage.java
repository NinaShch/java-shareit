package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ItemBadRequestException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryItemStorage implements ItemStorage {
    private final Map<Long, Item> itemMap = new HashMap<>();
    private Long id = 0L;

    private Long getNextId() {
        return ++id;
    }

    @Override
    public ItemDto getItemInfo(Long itemId, Long userId) {
        return ItemMapper.toItemDto(itemMap.get(itemId));
    }

    @Override
    public ItemDto add(ItemDto itemDto, Long userId) {
        if (itemDto.getName() == null || itemDto.getDescription() == null || itemDto.getAvailable() == null)
            throw new ItemBadRequestException("Attempt add item with absent fields");
        if (itemDto.getName().isBlank() || itemDto.getDescription().isBlank())
            throw new ItemBadRequestException("Attempt add item with absent fields");
        Long itemId = getNextId();
        itemDto.setId(itemId);
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(userId);
        itemMap.put(itemId, item);
        return itemDto;
    }

    @Override
    public ItemDto change(Long itemId, Long userId, ItemDto itemDto) {
        if (!itemMap.containsKey(itemId)) throw new ItemBadRequestException("Attempt update item with wrong id");
        if (itemDto.getId() != null && !Objects.equals(itemDto.getId(), itemId))
            throw new ItemBadRequestException("Attempt update item by id where id is not as in itemDto");
        Item item = itemMap.get(itemId);
        if (!Objects.equals(item.getOwner(), userId))
            throw new ItemNotFoundException("Attempt update item other user");
        if (itemDto.getName() != null) item.setName(itemDto.getName());
        if (itemDto.getDescription() != null) item.setDescription(itemDto.getDescription());
        if (itemDto.getAvailable() != null) item.setAvailable(itemDto.getAvailable());
        itemMap.put(itemId, item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public Collection<ItemDto> getById(Long userId) {
        return itemMap.values().stream()
                .filter(item -> Objects.equals(item.getOwner(), userId))
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ItemDto> getByKeyWords(String text) {
        if (text.isBlank()) return List.of();
        return itemMap.values().stream()
                .filter(item ->
                        item.getName().toLowerCase().contains(text.toLowerCase()) ||
                                item.getDescription().toLowerCase().contains(text.toLowerCase())
                )
                .filter(item -> item.getAvailable().equals(true))
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
