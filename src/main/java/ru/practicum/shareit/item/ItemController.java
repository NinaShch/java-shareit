package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

@Validated
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto addNewItem(
            @RequestBody ItemDto itemDto,
            @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        log.info("Request to add new item {} by user id = {}", itemDto, userId);
        return itemService.addNewItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto changeItem(@PathVariable Long itemId,
                              @RequestHeader("X-Sharer-User-Id") Long userId,
                              @RequestBody ItemDto itemDto) {
        log.info("Request to change item {}", itemDto);
        return itemService.changeItem(itemId, userId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemInfo(
            @PathVariable Long itemId,
            @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        log.info("Request item info by id, id = {}", itemId);
        return itemService.getItemInfo(itemId, userId);
    }

    @GetMapping
    public Collection<ItemDto> getAllItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                           @RequestParam(required = false) Integer from,
                                           @RequestParam(required = false) Integer size) {
        log.info("user id = {} requested list items", userId);
        return itemService.getItemsByUserId(userId, from, size);
    }

    @GetMapping("/search")
    public Collection<ItemDto> getItemsByKeyword(@RequestParam String text,
                                                 @RequestParam(required = false) Integer from,
                                                 @RequestParam(required = false) Integer size) {
        log.info("user finds item by keyword {}", text);
        return itemService.getItemsByKeyword(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto postComment(
            @PathVariable Long itemId,
            @RequestHeader("X-Sharer-User-Id") Long authorId,
            @RequestBody CommentDto commentDto
    ) {
        log.info("postComment, id = {}, comment = {}, userId = {}", itemId, commentDto.getText(), authorId);
        return itemService.postComment(itemId, authorId, commentDto);
    }

}
