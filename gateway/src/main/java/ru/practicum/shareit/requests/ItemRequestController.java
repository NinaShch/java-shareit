package ru.practicum.shareit.requests;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.requests.dto.ItemRequestDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

/**
 * A controller for Item Request.
 */
@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> addNew(
            @RequestBody ItemRequestDto itemRequestDto,
            @RequestHeader("X-Sharer-User-Id") long userId
    ) {
        log.info("Request to add new item request {} by user id = {}", itemRequestDto, userId);
        return itemRequestClient.addNew(itemRequestDto, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getByUserId(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("user id = {} requested list of requests", userId);
        return itemRequestClient.getByUserId(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAll(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size
    ) {
        log.info("getAll requests from={}, size={}", from, size);
        return itemRequestClient.getAll(userId, from, size);
    }

    @GetMapping("/{itemRequestId}")
    public ResponseEntity<Object> getById(
            @PathVariable long itemRequestId,
            @RequestHeader("X-Sharer-User-Id") long userId
    ) {
        log.info("Request item request id, id = {}", itemRequestId);
        return itemRequestClient.getById(itemRequestId, userId);
    }
}
