package ru.practicum.shareit.requests;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.requests.dto.ItemRequestDto;

import java.util.List;

/**
 * A controller for Item Request.
 */
@Validated
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto addNew(
            @RequestBody ItemRequestDto itemRequestDto,
            @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        log.info("Request to add new item request {} by user id = {}", itemRequestDto, userId);
        return itemRequestService.addNew(itemRequestDto, userId);
    }

    @GetMapping
    public List<ItemRequestDto> getByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("user id = {} requested list of requests", userId);
        return itemRequestService.getByUserId(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAll(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(required = false) Integer from,
            @RequestParam(required = false) Integer size
    ) {
        log.info("getAll requests from={}, size={}", from, size);
        return itemRequestService.getAll(userId, from, size);
    }

    @GetMapping("/{itemRequestId}")
    public ItemRequestDto getById(
            @PathVariable Long itemRequestId,
            @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        log.info("Request item request id, id = {}", itemRequestId);
        return itemRequestService.getById(itemRequestId, userId);
    }
}
