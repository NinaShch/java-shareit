package ru.practicum.shareit.requests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.practicum.shareit.TestUtil;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.paging.OffsetLimitPageable;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.storage.ItemRequestRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.AssertionErrors.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ItemRequestServiceTest {

    @InjectMocks
    private ItemRequestService underTest;

    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ItemRequest request;

    @Mock
    private User user;
    @Mock
    private User user2;

    private final Long userId = 123L;
    private final Long userId2 = 321L;

    @BeforeEach
    public void before() {
        when(userRepository.findById(eq(userId))).thenReturn(Optional.of(user));
        when(itemRepository.findByRequestId(Matchers.any())).thenReturn(new ArrayList<>());
        when(itemRequestRepository.save(Matchers.any())).thenAnswer(input -> input.getArguments()[0]);

        TestUtil.setupUserWithRepo(user, userId, "user", userRepository);
        TestUtil.setupUserWithRepo(user2, userId2, "user2", userRepository);
    }

    @Test
    public void bad_request_when_add_new_with_no_description() {
        assertThrows(
                BadRequestException.class,
                () -> {
                    ItemRequestDto itemRequestDto = new ItemRequestDto(null, null, null, null);
                    underTest.addNew(itemRequestDto, userId);
                }
        );
    }

    @Test
    public void not_found_when_add_new_with_no_user_found() {
        assertThrows(
                NotFoundException.class,
                () -> {
                    when(userRepository.findById(eq(userId))).thenReturn(Optional.empty());
                    ItemRequestDto itemRequestDto = new ItemRequestDto(null, "desc", null, null);
                    underTest.addNew(itemRequestDto, userId);
                }
        );
    }

    @Test
    public void saved_to_repo_when_add_new() {
        ItemRequestDto itemRequestDto = new ItemRequestDto(null, "desc", null, null);
        underTest.addNew(itemRequestDto, userId);

        ArgumentCaptor<ItemRequest> captor = ArgumentCaptor.forClass(ItemRequest.class);
        verify(itemRequestRepository).save(captor.capture());

        assertEquals("descriptions not match", "desc", captor.getValue().getDescription());
    }

    @Test
    public void return_added_when_add_new() {
        ItemRequestDto itemRequestDto = new ItemRequestDto(null, "desc", null, null);
        ItemRequestDto result = underTest.addNew(itemRequestDto, userId);

        assertEquals("descriptions not match", "desc", result.getDescription());
    }

    @Test
    public void has_created_when_add_new() {
        ItemRequestDto itemRequestDto = new ItemRequestDto(null, "desc", null, null);
        ItemRequestDto result = underTest.addNew(itemRequestDto, userId);

        assertNotNull("no created datetime info", result.getCreated());
    }

    @Test
    public void empty_items_when_add_new() {
        ItemRequestDto itemRequestDto = new ItemRequestDto(null, "desc", null, null);
        ItemRequestDto result = underTest.addNew(itemRequestDto, userId);

        assertNotNull("no created items data", result.getItems());
        assertTrue("items data is not empty", result.getItems().isEmpty());
    }

    @Test
    public void not_found_when_get_by_user_with_no_user_found() {
        assertThrows(
                NotFoundException.class,
                () -> {
                    when(userRepository.findById(eq(userId))).thenReturn(Optional.empty());
                    underTest.getByUserId(userId);
                }
        );
    }

    @Test
    public void got_empty_requests_when_get_by_user() {
        when(itemRequestRepository.findByRequestor(user)).thenReturn(new ArrayList<>());
        List<ItemRequestDto> result = underTest.getByUserId(userId);
        assertTrue("Results not empty", result.isEmpty());
    }

    @Test
    public void got_requests_when_get_by_user() {
        List<ItemRequest> requestsInDb = new ArrayList<>();
        requestsInDb.add(getMockItemRequest());
        requestsInDb.add(getMockItemRequest());
        requestsInDb.add(getMockItemRequest());
        when(itemRequestRepository.findByRequestor(user)).thenReturn(requestsInDb);
        List<ItemRequestDto> result = underTest.getByUserId(userId);
        assertEquals("Wrong result size", 3, result.size());
    }

    @Test
    public void get_all_with_no_paging() {
        List<ItemRequest> requestsInDb = new ArrayList<>();
        requestsInDb.add(getMockItemRequest());
        requestsInDb.add(getMockItemRequest());
        requestsInDb.add(getMockItemRequest());
        requestsInDb.add(getMockItemRequest());
        requestsInDb.add(getMockItemRequest());
        requestsInDb.add(getMockItemRequest());
        when(itemRequestRepository.findByRequestorNot(any(), any())).thenReturn(requestsInDb);
        List<ItemRequestDto> result = underTest.getAll(userId2, null, null);
        assertEquals("Wrong result size", 6, result.size());

        ArgumentCaptor<OffsetLimitPageable> captor = ArgumentCaptor.forClass(OffsetLimitPageable.class);
        verify(itemRequestRepository).findByRequestorNot(eq(user2), captor.capture());
        OffsetLimitPageable pageable = captor.getValue();
        assertEquals("wrong offset", 0L, pageable.getOffset());
        assertEquals("wrong page size", Integer.MAX_VALUE, pageable.getPageSize());
    }

    @Test
    public void get_all_with_paging() {
        List<ItemRequest> requestsInDb = new ArrayList<>();
        requestsInDb.add(getMockItemRequest());
        requestsInDb.add(getMockItemRequest());
        requestsInDb.add(getMockItemRequest());
        requestsInDb.add(getMockItemRequest());
        requestsInDb.add(getMockItemRequest());
        requestsInDb.add(getMockItemRequest());
        when(itemRequestRepository.findByRequestorNot(any(), any())).thenReturn(requestsInDb);
        List<ItemRequestDto> result = underTest.getAll(userId2, 1, 4);
        assertFalse("Wrong result size", result.isEmpty());

        ArgumentCaptor<OffsetLimitPageable> captor = ArgumentCaptor.forClass(OffsetLimitPageable.class);
        verify(itemRequestRepository).findByRequestorNot(eq(user2), captor.capture());
        OffsetLimitPageable pageable = captor.getValue();
        assertEquals("wrong offset", 1L, pageable.getOffset());
        assertEquals("wrong page size", 4, pageable.getPageSize());
    }

    @Test
    public void bad_request_when_no_paging_size() {
        assertThrows(
                BadRequestException.class,
                () -> underTest.getAll(userId2, 1, null)
        );
    }

    @Test
    public void bad_request_when_no_paging_from() {
        assertThrows(
                BadRequestException.class,
                () -> underTest.getAll(userId2, null, 3)
        );
    }

    @Test
    public void bad_request_when_negative_from() {
        assertThrows(
                BadRequestException.class,
                () -> underTest.getAll(userId2, -1, 3)
        );
    }

    @Test
    public void bad_request_when_negative_size() {
        assertThrows(
                BadRequestException.class,
                () -> underTest.getAll(userId2, 0, -1)
        );
    }

    @Test
    public void bad_request_when_zero_size() {
        assertThrows(
                BadRequestException.class,
                () -> underTest.getAll(userId2, 1, 0)
        );
    }

    private ItemRequest getMockItemRequest() {
        ItemRequest mock = mock(ItemRequest.class);
        when(mock.getRequestor()).thenReturn(user);
        return mock;
    }
}
