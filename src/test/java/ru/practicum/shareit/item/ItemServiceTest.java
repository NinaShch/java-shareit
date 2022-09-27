package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.practicum.shareit.TestUtil;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.storage.ItemRequestRepository;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.AssertionErrors.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ItemServiceTest {

    private static final long OWNER_ID = 111;
    private static final long REQUESTOR_ID = 222;
    private static final long ITEM_ID = 123L;
    private static final long REQUEST_ID = 101L;

    @InjectMocks
    private ItemServiceImpl underTest;

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private User owner;
    @Mock
    private User requestor;
    @Mock
    private Item item;
    @Mock
    private ItemRequest request;

    private final ItemDto input = new ItemDto(
            ITEM_ID,
            "item",
            "desc",
            true,
            new ArrayList<>(),
            null,
            null,
            null
    );

    @BeforeEach
    public void before() {
        when(itemRepository.save(any())).thenAnswer(input -> input.getArguments()[0]);
        when(itemRequestRepository.findById(eq(REQUEST_ID))).thenReturn(Optional.of(request));

        TestUtil.setupUserWithRepo(owner, OWNER_ID, "owner", userRepository);
        TestUtil.setupUserWithRepo(requestor, REQUESTOR_ID, "requestor", userRepository);
        TestUtil.setupItemWithRepo(item, ITEM_ID, "item", owner, itemRepository);
        TestUtil.setupItemRequestWithRepo(request, REQUEST_ID, requestor, itemRequestRepository);
    }

    @Test
    public void throw_not_found_when_no_such_user_on_add() {
        assertThrows(
                NotFoundException.class,
                () -> {
                    when(userRepository.findById(eq(OWNER_ID))).thenReturn(Optional.empty());
                    underTest.addNewItem(input, OWNER_ID);
                }
        );
    }

    @Test
    public void throw_bad_request_when_no_name() {
        assertThrows(
                BadRequestException.class,
                () -> {
                    input.setName(null);
                    underTest.addNewItem(input, OWNER_ID);
                }
        );
    }

    @Test
    public void throw_bad_request_when_no_description() {
        assertThrows(
                BadRequestException.class,
                () -> {
                    input.setDescription(null);
                    underTest.addNewItem(input, OWNER_ID);
                }
        );
    }

    @Test
    public void throw_bad_request_when_no_availability_data() {
        assertThrows(
                BadRequestException.class,
                () -> {
                    input.setAvailable(null);
                    underTest.addNewItem(input, OWNER_ID);
                }
        );
    }

    @Test
    public void throw_bad_request_when_name_is_empty() {
        assertThrows(
                BadRequestException.class,
                () -> {
                    input.setName("");
                    underTest.addNewItem(input, OWNER_ID);
                }
        );
    }

    @Test
    public void throw_bad_request_when_name_is_blank() {
        assertThrows(
                BadRequestException.class,
                () -> {
                    input.setName("   ");
                    underTest.addNewItem(input, OWNER_ID);
                }
        );
    }

    @Test
    public void throw_bad_request_when_description_is_empty() {
        assertThrows(
                BadRequestException.class,
                () -> {
                    input.setDescription("");
                    underTest.addNewItem(input, OWNER_ID);
                }
        );
    }

    @Test
    public void throw_bad_request_when_description_is_blank() {
        assertThrows(
                BadRequestException.class,
                () -> {
                    input.setDescription("   ");
                    underTest.addNewItem(input, OWNER_ID);
                }
        );
    }

    @Test
    public void saved_to_repo_when_added() {
        underTest.addNewItem(input, OWNER_ID);

        ArgumentCaptor<Item> captor = ArgumentCaptor.forClass(Item.class);
        verify(itemRepository).save(captor.capture());

        Item savedItem = captor.getValue();
        assertEquals("ids not match", ITEM_ID, savedItem.getId());
        assertEquals("names not match", input.getName(), savedItem.getName());
        assertEquals("descriptions not match", input.getDescription(), savedItem.getDescription());
        assertEquals("availability data not match", input.getAvailable(), savedItem.getAvailable());
        assertEquals("owners not match", owner, savedItem.getOwner());
    }

    @Test
    public void returned_when_added() {
        ItemDto result = underTest.addNewItem(input, OWNER_ID);

        assertEquals("ids not match", (Long) ITEM_ID, result.getId());
        assertEquals("names not match", input.getName(), result.getName());
        assertEquals("descriptions not match", input.getDescription(), result.getDescription());
        assertEquals("availability data not match", input.getAvailable(), result.getAvailable());
    }

    @Test
    public void no_item_request_by_default() {
        underTest.addNewItem(input, OWNER_ID);

        ArgumentCaptor<Item> captor = ArgumentCaptor.forClass(Item.class);
        verify(itemRepository).save(captor.capture());

        Item savedItem = captor.getValue();
        assertNull("unexpected item request", savedItem.getRequest());
    }

    @Test
    public void item_request_processed_correctly() {
        input.setRequestId(REQUEST_ID);
        underTest.addNewItem(input, OWNER_ID);

        ArgumentCaptor<Item> captor = ArgumentCaptor.forClass(Item.class);
        verify(itemRepository).save(captor.capture());

        Item savedItem = captor.getValue();
        assertEquals("availability data not match", request, savedItem.getRequest());
    }

    @Test
    public void no_item_request_when_no_such_request() {
        when(itemRequestRepository.findById(REQUEST_ID)).thenReturn(Optional.empty());
        input.setRequestId(REQUEST_ID);
        underTest.addNewItem(input, OWNER_ID);

        ArgumentCaptor<Item> captor = ArgumentCaptor.forClass(Item.class);
        verify(itemRepository).save(captor.capture());

        Item savedItem = captor.getValue();
        assertNull("unexpected item request", savedItem.getRequest());
    }

    @Test
    public void get_items_by_keyword_invokes_search() {
        underTest.getItemsByKeyword("text123", null, null);
        verify(itemRepository).search(eq("text123"), any());
    }

    @Test
    public void get_items_by_keyword_invokes_search_with_lowercase() {
        underTest.getItemsByKeyword("TEXT123a", null, null);
        verify(itemRepository).search(eq("text123a"), any());
    }

    @Test
    public void get_items_by_keyword_returns_empty_list_on_empty_request() {
        Collection<ItemDto> result = underTest.getItemsByKeyword("", null, null);
        assertTrue("results are not empty", result.isEmpty());
    }

    @Test
    public void forbiddenException_is_thrown_when_item_is_changed_not_by_owner() {
        assertThrows(
                ForbiddenException.class,
                () -> {
                    underTest.changeItem(ITEM_ID, REQUESTOR_ID, input);
                }
        );
    }

    @Test
    public void saved_to_repo_when_item_is_changed() {
        when(itemRepository.getItemOwner(eq(ITEM_ID))).thenReturn(owner);
        underTest.changeItem(ITEM_ID, OWNER_ID, input);
        verify(itemRepository).save(eq(item));
    }

    @Test
    public void saved_to_repo_when_comment_is_posted() {
        Booking booking = new Booking(362L, LocalDateTime.now(), LocalDateTime.now(), item, requestor,
                Status.APPROVED);
        List<Booking> bookingList = new ArrayList<>();
        bookingList.add(booking);

        when(bookingRepository.findByBookerAndItem(eq(requestor), eq(item)))
                .thenReturn(bookingList);
        underTest.postComment(
                ITEM_ID,
                REQUESTOR_ID,
                new CommentDto(
                        null,
                        "commentText",
                        "authorName",
                        null
                )
        );
        ArgumentCaptor<Comment> captor = ArgumentCaptor.forClass(Comment.class);
        verify(commentRepository).save(captor.capture());
        Comment comment = captor.getValue();

        assertEquals("commentText is wrong", "commentText", comment.getText());
    }
}
