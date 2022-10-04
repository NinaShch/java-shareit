package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> addNewItem(
            @RequestBody @Valid ItemDto itemDto,
            @RequestHeader("X-Sharer-User-Id") long userId
    ) {
        log.info("Request to add new item {} by user id = {}", itemDto, userId);
        return itemClient.addNewItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> changeItem(
            @PathVariable long itemId,
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestBody @Valid ItemDto itemDto
    ) {
        log.info("Request to change item {}", itemDto);
        return itemClient.changeItem(itemId, userId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemInfo(
            @PathVariable long itemId,
            @RequestHeader("X-Sharer-User-Id") long userId
    ) {
        log.info("Request item info by id, id = {}", itemId);
        return itemClient.getItemInfo(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItems(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size
    ) {
        log.info("user id = {} requested list items", userId);
        return itemClient.getItemsByUserId(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getItemsByKeyword(
            @RequestParam String text,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size
    ) {
        log.info("user finds item by keyword {}", text);
        return itemClient.getItemsByKeyword(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> postComment(
            @PathVariable long itemId,
            @RequestHeader("X-Sharer-User-Id") long authorId,
            @RequestBody CommentDto commentDto
    ) {
        log.info("postComment, id = {}, comment = {}, userId = {}", itemId, commentDto.getText(), authorId);
        return itemClient.postComment(itemId, authorId, commentDto);
    }
}
