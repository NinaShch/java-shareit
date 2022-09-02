package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ItemBadRequestException;
import ru.practicum.shareit.exception.ItemNotFoundException;
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
    public Item getItemInfo(Long itemId) {
        return itemMap.get(itemId);
    }

    @Override
    public Item add(Item item) {
        Long itemId = getNextId();
        item.setId(itemId);
        itemMap.put(itemId, item);
        return item;
    }

    @Override
    public Item change(Long itemId, Long userId, Item itemUpdate) {
        if (!itemMap.containsKey(itemId)) throw new ItemBadRequestException("Attempt update item with wrong id");
        Item item = itemMap.get(itemId);
        if (!Objects.equals(item.getOwner().getId(), userId))
            throw new ItemNotFoundException("Attempt update item other user");
        if (itemUpdate.getName() != null) item.setName(itemUpdate.getName());
        if (itemUpdate.getDescription() != null) item.setDescription(itemUpdate.getDescription());
        if (itemUpdate.getAvailable() != null) item.setAvailable(itemUpdate.getAvailable());
        itemMap.put(itemId, item);
        return item;
    }

    @Override
    public Collection<Item> getById(Long userId) {
        return itemMap.values();
    }

    @Override
    public Collection<Item> getByKeyWords(String text) {
        if (text.isBlank()) return List.of();
        return itemMap.values().stream()
                .filter(item ->
                        item.getName().toLowerCase().contains(text.toLowerCase()) ||
                                item.getDescription().toLowerCase().contains(text.toLowerCase())
                )
                .filter(item -> item.getAvailable().equals(true))
                .collect(Collectors.toList());
    }
}
