package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.paging.OffsetLimitPageable;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.storage.ItemRequestRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    public ItemDto getItemInfo(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("item not found"));
        return ItemMapper.toItemDto(
                item,
                getComments(itemId),
                getLastBooking(item, userId),
                getNextBooking(item, userId)
        );
    }

    @Override
    public ItemDto addNewItem(ItemDto itemDto, Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) throw new NotFoundException("User not found");
        if (itemDto.getName() == null || itemDto.getDescription() == null || itemDto.getAvailable() == null)
            throw new BadRequestException("Attempt add item with absent fields");
        if (itemDto.getName().isBlank() || itemDto.getDescription().isBlank())
            throw new BadRequestException("Attempt add item with absent fields");
        Long requestId = itemDto.getRequestId();
        ItemRequest itemRequest = requestId != null ? itemRequestRepository.findById(requestId).orElse(null) : null;
        Item item = itemRepository.save(ItemMapper.toItem(itemDto, user.get(), itemRequest));
        return ItemMapper.toItemDto(item,
                getComments(item.getId()),
                getLastBooking(item, userId),
                getNextBooking(item, userId));
    }

    @Override
    public ItemDto changeItem(Long itemId, Long userId, ItemDto itemDto) {
        User user = getUser(userId);
        Item item = getItem(itemId);

        if (itemDto.getId() != null && !Objects.equals(itemDto.getId(), itemId))
            throw new BadRequestException("Attempt update item by id where id is not as in itemDto");
        if (!user.equals(itemRepository.getItemOwner(itemId)))
            throw new ForbiddenException("Attempt update item not by owner");

        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }

        return ItemMapper.toItemDto(itemRepository.save(item),
                getComments(item.getId()),
                getLastBooking(item, item.getOwner().getId()),
                getNextBooking(item, item.getOwner().getId()));
    }

    @Override
    public Collection<ItemDto> getItemsByUserId(Long userId, Integer from, Integer size) {
        Pageable pageable = OffsetLimitPageable.create(from, size, Sort.by(Sort.Direction.ASC, "id"));
        User user = getUser(userId);
        return itemRepository.findByOwner(user, pageable).stream()
                .map(item -> ItemMapper.toItemDto(item,
                        getComments(item.getId()),
                        getLastBooking(item, item.getOwner().getId()),
                        getNextBooking(item, item.getOwner().getId())))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ItemDto> getItemsByKeyword(String text, Integer from, Integer size) {
        Pageable pageable = OffsetLimitPageable.create(from, size, Sort.by(Sort.Direction.ASC, "id"));
        if (text.isBlank()) return new ArrayList<>();
        return itemRepository.search(text.toLowerCase(), pageable).stream()
                .map(item -> ItemMapper.toItemDto(item,
                        getComments(item.getId()),
                        getLastBooking(item, item.getOwner().getId()),
                        getNextBooking(item, item.getOwner().getId())))
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto postComment(Long itemId, Long authorId, CommentDto commentDto) {
        Item item = getItem(itemId);
        User author = getUser(authorId);
        if (commentDto.getText().isBlank()) {
            throw new BadRequestException("Cant post blank comments");
        }
        if (!wasItemBookedByUser(item, author)) {
            throw new BadRequestException("Cant comment without finished booking");
        }
        commentDto.setCreated(LocalDateTime.now());
        Comment comment = CommentMapper.toComment(commentDto, author, item);
        commentRepository.save(comment);
        return CommentMapper.toCommentDto(comment);
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("user not found"));
    }

    private Item getItem(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("item not found"));
    }

    private List<CommentDto> getComments(Long itemId) {
        return commentRepository.findByItemId(itemId).stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    private boolean wasItemBookedByUser(Item item, User user) {
        return bookingRepository.findByBookerAndItem(user, item)
                .stream()
                .anyMatch((booking) -> booking.getEnd().isBefore(LocalDateTime.now()));
    }

    private ItemDto.ExtremumBookingDto getLastBooking(Item item, Long userId) {
        if (!item.getOwner().getId().equals(userId)) return null;
        List<Booking> bookings = bookingRepository.findByItemAndEndIsBefore(item, LocalDateTime.now());
        if (bookings.isEmpty()) {
            return null;
        }
        return BookingMapper.toExtremumBookingDto(bookings.get(bookings.size() - 1));
    }

    private ItemDto.ExtremumBookingDto getNextBooking(Item item, Long userId) {
        if (!item.getOwner().getId().equals(userId)) return null;
        List<Booking> bookings = bookingRepository.findByItemAndStartIsAfter(item, LocalDateTime.now());
        if (bookings.isEmpty()) {
            return null;
        }
        return BookingMapper.toExtremumBookingDto(bookings.get(0));
    }
}
