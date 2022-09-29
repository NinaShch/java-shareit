package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class BookingControllerTest {

    @Mock
    private BookingService bookingService;

    @Mock
    private ItemRequest itemRequest;

    @InjectMocks
    private BookingController bookingController;

    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    private MockMvc mvc;
    private BookingInputDto bookingInputDto;
    private BookingOutputDto bookingOutputDto;
    private Booking booking;
    private static final LocalDateTime START = LocalDateTime.of(2032, 9, 15, 9, 19);
    private static final LocalDateTime END = LocalDateTime.of(2033, 1, 1, 0, 0);
    private static final User user = new User(2L, "name", "email@yandex.ru");
    private final Item item = new Item(3L, "item", "description", true, user, itemRequest);

    @BeforeEach
    void before() {
        mvc = MockMvcBuilders
                .standaloneSetup(bookingController)
                .build();

        bookingInputDto = new BookingInputDto(
                1L,
                START,
                END
        );

        bookingOutputDto = new BookingOutputDto(
                1L,
                START,
                END,
                item,
                user,
                Status.WAITING
        );

        booking = new Booking(
                1L,
                START,
                END,
                item,
                user,
                Status.WAITING
        );
    }

    @Test
    void addNewBooking() throws Exception {
        when(bookingService.addNewBooking(any(), anyLong())).thenReturn(booking);

        long userId = 123L;
        mvc.perform(post("/bookings")
                .content(mapper.writeValueAsString(bookingInputDto))
                .header("X-Sharer-User-Id", userId)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.item.id", is(bookingOutputDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(bookingOutputDto.getItem().getName()), String.class))
                .andExpect(jsonPath("$.booker.id", is(bookingOutputDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.status", is("WAITING")));

        verify(bookingService).addNewBooking(eq(bookingInputDto), eq(userId));
    }

    @Test
    void approve() throws Exception {
        when(bookingService.setBookingApproveStatus(anyLong(), anyLong(), anyBoolean())).thenReturn(booking);

        long userId = 123L;
        long bookingId = 321L;
        mvc.perform(patch("/bookings/" + bookingId + "?approved=true")
                .header("X-Sharer-User-Id", userId)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.item.id", is(bookingOutputDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(bookingOutputDto.getItem().getName()), String.class))
                .andExpect(jsonPath("$.booker.id", is(bookingOutputDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.status", is("WAITING")));

        verify(bookingService).setBookingApproveStatus(eq(bookingId), eq(userId), eq(true));
    }

    @Test
    void getBookingInfo() throws Exception {
        when(bookingService.getBookingInfo(anyLong(), anyLong())).thenReturn(booking);

        long userId = 123L;
        long bookingId = 321L;
        mvc.perform(get("/bookings/" + bookingId)
                .header("X-Sharer-User-Id", userId)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.item.id", is(bookingOutputDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(bookingOutputDto.getItem().getName()), String.class))
                .andExpect(jsonPath("$.booker.id", is(bookingOutputDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.status", is("WAITING")));

        verify(bookingService).getBookingInfo(eq(bookingId), eq(userId));
    }

    @Test
    void getAllBookings() throws Exception {
        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking);

        when(bookingService.getAllBookings(anyLong(), any(), any(), any())).thenReturn(bookings);

        long userId = 123L;
        mvc.perform(get("/bookings")
                .header("X-Sharer-User-Id", userId)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(bookingService).getAllBookings(eq(userId), eq(BookingState.ALL), isNull(), isNull());
    }

    @Test
    void getAllBookingsForOwner() throws Exception {
        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking);

        when(bookingService.getAllBookingsForOwner(anyLong(), any(), any(), any())).thenReturn(bookings);

        long userId = 123L;
        mvc.perform(get("/bookings/owner")
                .header("X-Sharer-User-Id", userId)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(bookingService).getAllBookingsForOwner(eq(userId), eq(BookingState.ALL), isNull(), isNull());
    }

    @Test
    void getAllBookingsWithPaging() throws Exception {
        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking);

        when(bookingService.getAllBookings(anyLong(), any(), any(), any())).thenReturn(bookings);

        long userId = 123L;
        mvc.perform(get("/bookings?from=1&size=20")
                .header("X-Sharer-User-Id", userId)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(bookingService).getAllBookings(eq(userId), eq(BookingState.ALL), eq(1), eq(20));
    }

    @Test
    void getAllBookingsForOwnerWithPaging() throws Exception {
        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking);

        when(bookingService.getAllBookingsForOwner(anyLong(), any(), any(), any())).thenReturn(bookings);

        long userId = 123L;
        mvc.perform(get("/bookings/owner?from=1&size=20")
                .header("X-Sharer-User-Id", userId)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(bookingService).getAllBookingsForOwner(eq(userId), eq(BookingState.ALL), eq(1), eq(20));
    }
}
