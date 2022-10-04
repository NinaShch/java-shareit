package ru.practicum.shareit.requests;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.paging.OffsetLimitPageable;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.storage.ItemRequestRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequestDto addNew(ItemRequestDto itemRequestDto, Long userId) {
        User user = getUser(userId);
        if (itemRequestDto.getDescription() == null)
            throw new BadRequestException("Attempt add item request with absent description");

        ItemRequest request = itemRequestRepository.save(
                ItemRequestMapper.toItemRequest(itemRequestDto, user));
        return ItemRequestMapper.toItemRequestDto(request, itemRepository.findByRequestId(request.getId()));
    }

    @Override
    public List<ItemRequestDto> getByUserId(Long userId) {
        User user = getUser(userId);
        return itemRequestRepository.findByRequestor(user).stream()
                .filter(request -> Objects.equals(request.getRequestor().getId(), userId))
                .map(request -> ItemRequestMapper.toItemRequestDto(request, itemRepository.findByRequestId(request.getId())))
                .sorted(Comparator.comparing(ItemRequestDto::getId))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getAll(Long userId, Integer from, Integer size) {
        User user = getUser(userId);
        return itemRequestRepository
                .findByRequestorNot(user,
                        OffsetLimitPageable.create(from, size, Sort.by(Sort.Direction.DESC, "created")))
                .stream()
                .map(request -> ItemRequestMapper.toItemRequestDto(request, itemRepository.findByRequestId(request.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getById(Long itemRequestId, Long userId) {
        getUser(userId); // just validate
        ItemRequest request = itemRequestRepository.findById(itemRequestId).orElseThrow(
                () ->  new NotFoundException("no such itemRequestId"));
        return ItemRequestMapper.toItemRequestDto(request, itemRepository.findByRequestId(request.getId()));
    }

    @Override
    public User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("user not found"));
    }
}
