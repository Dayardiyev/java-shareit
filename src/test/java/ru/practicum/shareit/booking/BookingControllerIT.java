package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingCreateRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.common.ObjectGenerator;
import ru.practicum.shareit.common.exception.NotAvailableException;
import ru.practicum.shareit.common.exception.NotFoundException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({BookingController.class, BookingMapper.class, ObjectGenerator.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingControllerIT {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final ObjectGenerator objectGenerator;

    @MockBean
    private BookingService bookingService;

    private LocalDateTime currentDateTime;
    private BookingCreateRequest bookingCreateRequest;
    private Comment comment;
    private Booking booking;
    private Item item;
    private User booker;
    private User owner;

    @BeforeEach
    void setUp() {
        currentDateTime = LocalDateTime.now();

        bookingCreateRequest = objectGenerator.next(BookingCreateRequest.class);

        owner = objectGenerator.next(User.class);
        booker = objectGenerator.next(User.class);
        item = objectGenerator.next(Item.class);
        booking = objectGenerator.next(Booking.class);
        comment = objectGenerator.next(Comment.class);

        owner.setId(1L);
        booker.setId(2L);

        item.setId(1L);
        item.setComments(Set.of(comment));
        item.setLastBooking(booking);

        booking.setId(1L);
        booking.setItem(item);
        booking.setBooker(booker);

        comment.setId(1L);
        comment.setAuthor(booker);
    }


    @SneakyThrows
    @Test
    void create_whenValidBookingCreateRequest_thenBookingResponseReturned() {
        long userId = 1L;
        bookingCreateRequest.setStart(currentDateTime.plusDays(1));
        bookingCreateRequest.setEnd(currentDateTime.plusDays(2));
        bookingCreateRequest.setItemId(1L);

        when(bookingService.create(anyLong(), any()))
                .thenReturn(booking);

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(bookingCreateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.item.id", is(item.getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(item.getName())))
                .andExpect(jsonPath("$.item.description", is(item.getDescription())))
                .andExpect(jsonPath("$.item.available", is(item.getAvailable())))
                .andExpect(jsonPath("$.item.requestId").hasJsonPath())
                .andExpect(jsonPath("$.item.comments").hasJsonPath())
                .andExpect(jsonPath("$.item.nextBooking").hasJsonPath())
                .andExpect(jsonPath("$.item.lastBooking").hasJsonPath())
                .andExpect(jsonPath("$.booker").hasJsonPath())
                .andExpect(jsonPath("$.status").hasJsonPath())
                .andExpect(jsonPath("$.start").hasJsonPath())
                .andExpect(jsonPath("$.end").hasJsonPath());

        verify(bookingService).create(anyLong(), any());
    }


    @SneakyThrows
    @Test
    void create_whenMissingUserIdInHeader_thenBadRequestReturned() {
        long userId = 1L;
        String errorMessage =
                "Required request header 'X-Sharer-User-Id' for method parameter type Long is not present";
        bookingCreateRequest.setStart(currentDateTime.plusDays(1));
        bookingCreateRequest.setEnd(currentDateTime.plusDays(2));
        bookingCreateRequest.setItemId(1L);

        when(bookingService.create(anyLong(), any()))
                .thenReturn(booking);

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(bookingCreateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason(containsString(errorMessage)));

        verify(bookingService, never()).create(anyLong(), any());
    }

    @SneakyThrows
    @Test
    void create_whenItemIdIsNull_thenBadRequestReturned() {
        long userId = 1L;
        String errorMessage = "Идентификатор вещи не может быть пустым.";
        bookingCreateRequest.setStart(currentDateTime.plusDays(1));
        bookingCreateRequest.setEnd(currentDateTime.plusDays(2));
        bookingCreateRequest.setItemId(null);

        when(bookingService.create(anyLong(), any()))
                .thenReturn(booking);

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(bookingCreateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", Matchers.containsString(errorMessage)));

        verify(bookingService, never()).create(anyLong(), any());
    }

    @SneakyThrows
    @Test
    void create_whenStarDateIsNull_thenBadRequestReturned() {
        long userId = 1L;
        String errorMessage = "Дата начала бронирования не должна быть пустой";
        bookingCreateRequest.setStart(null);
        bookingCreateRequest.setEnd(currentDateTime.plusDays(2));
        bookingCreateRequest.setItemId(1L);

        when(bookingService.create(anyLong(), any()))
                .thenReturn(booking);

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(bookingCreateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", Matchers.containsString(errorMessage)));

        verify(bookingService, never()).create(anyLong(), any());
    }

    @SneakyThrows
    @Test
    void create_whenStartDateInPast_thenBadRequestReturned() {
        long userId = 1L;
        String errorMessage = "Дата начала бронирования должна быть в настоящем или будущем";
        bookingCreateRequest.setStart(currentDateTime.minusDays(1));
        bookingCreateRequest.setEnd(currentDateTime.plusDays(2));
        bookingCreateRequest.setItemId(1L);

        when(bookingService.create(anyLong(), any()))
                .thenReturn(booking);

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(bookingCreateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", Matchers.containsString(errorMessage)));

        verify(bookingService, never()).create(anyLong(), any());
    }

    @SneakyThrows
    @Test
    void create_whenEndIsNull_thenBadRequestReturned() {
        long userId = 1L;
        String errorMessage = "Дата окончания бронирования не должна быть пустой";
        bookingCreateRequest.setStart(currentDateTime.plusDays(1));
        bookingCreateRequest.setEnd(null);
        bookingCreateRequest.setItemId(1L);

        when(bookingService.create(anyLong(), any()))
                .thenReturn(booking);

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(bookingCreateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", Matchers.containsString(errorMessage)));

        verify(bookingService, never()).create(anyLong(), any());
    }


    @SneakyThrows
    @Test
    void create_whenEndIsCurrentDateTime_thenBadRequestReturned() {
        long userId = 1L;
        String errorMessage = "Дата окончания бронирования должна быть в будущем";
        bookingCreateRequest.setStart(currentDateTime.plusDays(1));
        bookingCreateRequest.setEnd(currentDateTime);
        bookingCreateRequest.setItemId(1L);

        when(bookingService.create(anyLong(), any()))
                .thenReturn(booking);

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(bookingCreateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", Matchers.containsString(errorMessage)));

        verify(bookingService, never()).create(anyLong(), any());
    }

    @SneakyThrows
    @Test
    void create_whenItemIsNotAvailable_thenBadRequestReturned() {
        long userId = 1L;
        bookingCreateRequest.setStart(currentDateTime.plusDays(1));
        bookingCreateRequest.setEnd(currentDateTime.plusDays(2));
        bookingCreateRequest.setItemId(1L);

        when(bookingService.create(anyLong(), any()))
                .thenThrow(NotAvailableException.class);

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(bookingCreateRequest)))
                .andExpect(status().isBadRequest());

        verify(bookingService).create(anyLong(), any());
    }

    @SneakyThrows
    @Test
    void approve_whenValidRequest_thenOkStatusReturned() {
        long userId = 1L;
        long bookingId = 1L;
        boolean approved = true;

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", userId)
                        .param("approved", Boolean.toString(approved)))
                .andExpect(status().isOk());

        verify(bookingService).approve(userId, bookingId, approved);
    }

    @SneakyThrows
    @Test
    void approve_whenMissingUserIdInHeader_thenBadRequestReturned() {
        long userId = 1L;
        long bookingId = 1L;
        boolean approved = true;

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .param("approved", Boolean.toString(approved)))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).approve(userId, bookingId, approved);
    }

    @SneakyThrows
    @Test
    void getById_whenValidRequest_thenBookingResponseReturned() {
        long userId = 1L;
        long bookingId = 1L;

        when(bookingService.findByUserIdAndId(bookingId, userId))
                .thenReturn(booking);

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.item.id", is(item.getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(item.getName())))
                .andExpect(jsonPath("$.item.description", is(item.getDescription())))
                .andExpect(jsonPath("$.item.available", is(item.getAvailable())))
                .andExpect(jsonPath("$.item.requestId").hasJsonPath())
                .andExpect(jsonPath("$.item.comments").hasJsonPath())
                .andExpect(jsonPath("$.item.nextBooking").hasJsonPath())
                .andExpect(jsonPath("$.item.lastBooking").hasJsonPath())
                .andExpect(jsonPath("$.booker").hasJsonPath())
                .andExpect(jsonPath("$.status").hasJsonPath())
                .andExpect(jsonPath("$.start").hasJsonPath())
                .andExpect(jsonPath("$.end").hasJsonPath());

        verify(bookingService).findByUserIdAndId(bookingId, userId);
    }

    @SneakyThrows
    @Test
    void getById_whenMissingUserIdInHeader_thenBadRequestReturned() {
        long userId = 1L;
        long bookingId = 1L;

        when(bookingService.findByUserIdAndId(bookingId, userId))
                .thenReturn(booking);

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).findByUserIdAndId(bookingId, userId);
    }

    @SneakyThrows
    @Test
    void getById_whenUserShouldNotHaveAccess_thenBadRequestReturned() {
        long userId = 1L;
        long bookingId = 1L;

        when(bookingService.findByUserIdAndId(bookingId, userId))
                .thenThrow(NotFoundException.class);

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isNotFound());

        verify(bookingService).findByUserIdAndId(bookingId, userId);
    }

    @SneakyThrows
    @Test
    void getByBookerId_whenValidRequest_thenListOfBookingResponseReturned() {
        long userId = 0L;
        BookingState stateFilter = BookingState.APPROVED;
        int from = 0;
        int size = 50;

        when(bookingService.findAllByBookerId(userId, stateFilter, from, size))
                .thenReturn(List.of(booking));

        mockMvc.perform(get("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", userId)
                        .param("state", stateFilter.name())
                        .param("from", Long.toString(from))
                        .param("size", Integer.toString(size))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1L), Long.class))
                .andExpect(jsonPath("$[0].item.id", is(item.getId()), Long.class))
                .andExpect(jsonPath("$[0].item.name", is(item.getName())))
                .andExpect(jsonPath("$[0].item.description", is(item.getDescription())))
                .andExpect(jsonPath("$[0].item.available", is(item.getAvailable())))
                .andExpect(jsonPath("$[0].item.requestId").hasJsonPath())
                .andExpect(jsonPath("$[0].item.comments").hasJsonPath())
                .andExpect(jsonPath("$[0].item.nextBooking").hasJsonPath())
                .andExpect(jsonPath("$[0].item.lastBooking").hasJsonPath())
                .andExpect(jsonPath("$[0].booker").hasJsonPath())
                .andExpect(jsonPath("$[0].status").hasJsonPath())
                .andExpect(jsonPath("$[0].start").hasJsonPath())
                .andExpect(jsonPath("$[0].end").hasJsonPath());

        verify(bookingService).findAllByBookerId(userId, stateFilter, from, size);
    }

    @SneakyThrows
    @Test
    void getByBookerId_whenInvalidStateInParameter_thenBadRequestReturned() {
        long userId = 0L;
        String invalidState = "RANDOM STATE";
        long from = 0;
        int size = 50;
        String errorMessage = "No enum constant";

        mockMvc.perform(get("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", userId)
                        .param("state", invalidState)
                        .param("from", Long.toString(from))
                        .param("size", Integer.toString(size))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", Matchers.containsString(errorMessage)));

        verify(bookingService, never()).findAllByBookerId(anyLong(), any(), anyInt(), anyInt());
    }

    @SneakyThrows
    @Test
    void getByBookerId_whenNegativeFromInParameter_thenBadRequestReturned() {
        long userId = 0L;
        long from = -1;
        int size = 50;
        String stateFilter = BookingState.APPROVED.name();
        String errorMessage = "must be greater than or equal to 0";

        mockMvc.perform(get("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", userId)
                        .param("state", stateFilter)
                        .param("from", Long.toString(from))
                        .param("size", Integer.toString(size))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", Matchers.containsString(errorMessage)));

        verify(bookingService, never()).findAllByBookerId(anyLong(), any(), anyInt(), anyInt());
    }

    @SneakyThrows
    @Test
    void getByBookerId_whenZeroSizeInParameter_thenBadRequestReturned() {
        long userId = 0L;
        long from = 0;
        int size = 0;
        String stateFilter = BookingState.APPROVED.name();
        String errorMessage = "must be greater than 0";

        mockMvc.perform(get("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", userId)
                        .param("state", stateFilter)
                        .param("from", Long.toString(from))
                        .param("size", Integer.toString(size))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", Matchers.containsString(errorMessage)));

        verify(bookingService, never()).findAllByBookerId(anyLong(), any(), anyInt(), anyInt());
    }

    @SneakyThrows
    @Test
    void getByOwner_whenValidRequest_thenListOfBookingResponseReturned() {
        long userId = 0L;
        BookingState stateFilter = BookingState.APPROVED;
        int from = 0;
        int size = 50;

        when(bookingService.findAllByOwnerId(userId, stateFilter, from, size))
                .thenReturn(List.of(booking));

        mockMvc.perform(get("/bookings/owner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", userId)
                        .param("state", stateFilter.name())
                        .param("from", Integer.toString(from))
                        .param("size", Integer.toString(size))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1L), Long.class))
                .andExpect(jsonPath("$[0].item.id", is(item.getId()), Long.class))
                .andExpect(jsonPath("$[0].item.name", is(item.getName())))
                .andExpect(jsonPath("$[0].item.description", is(item.getDescription())))
                .andExpect(jsonPath("$[0].item.available", is(item.getAvailable())))
                .andExpect(jsonPath("$[0].item.requestId").hasJsonPath())
                .andExpect(jsonPath("$[0].item.comments").hasJsonPath())
                .andExpect(jsonPath("$[0].item.nextBooking").hasJsonPath())
                .andExpect(jsonPath("$[0].item.lastBooking").hasJsonPath())
                .andExpect(jsonPath("$[0].booker").hasJsonPath())
                .andExpect(jsonPath("$[0].status").hasJsonPath())
                .andExpect(jsonPath("$[0].start").hasJsonPath())
                .andExpect(jsonPath("$[0].end").hasJsonPath());

        verify(bookingService).findAllByOwnerId(userId, stateFilter, from, size);
    }

    @SneakyThrows
    @Test
    void getByOwner_whenInvalidStateInParameter_thenBadRequestReturned() {
        long userId = 0L;
        String invalidState = "RANDOM STATE";
        int from = 0;
        int size = 50;
        String errorMessage = "No enum constant";

        mockMvc.perform(get("/bookings/owner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", userId)
                        .param("state", invalidState)
                        .param("from", Long.toString(from))
                        .param("size", Integer.toString(size))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", Matchers.containsString(errorMessage)));

        verify(bookingService, never()).findAllByOwnerId(anyLong(), any(), anyInt(), anyInt());
    }

    @SneakyThrows
    @Test
    void getByOwner_whenNegativeFromInParameter_thenBadRequestReturned() {
        long userId = 0L;
        int from = -1;
        int size = 50;
        String stateFilter = BookingState.APPROVED.name();
        String errorMessage = "must be greater than or equal to 0";

        mockMvc.perform(get("/bookings/owner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", userId)
                        .param("state", stateFilter)
                        .param("from", Integer.toString(from))
                        .param("size", Integer.toString(size))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", Matchers.containsString(errorMessage)));

        verify(bookingService, never()).findAllByOwnerId(anyLong(), any(), anyInt(), anyInt());
    }

    @SneakyThrows
    @Test
    void getByOwner_whenZeroSizeInParameter_thenBadRequestReturned() {
        long userId = 0L;
        int from = 0;
        int size = 0;
        String stateFilter = BookingState.APPROVED.name();
        String errorMessage = "must be greater than 0";

        mockMvc.perform(get("/bookings/owner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", userId)
                        .param("state", stateFilter)
                        .param("from", Integer.toString(from))
                        .param("size", Integer.toString(size))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", Matchers.containsString(errorMessage)));

        verify(bookingService, never()).findAllByOwnerId(anyLong(), any(), anyInt(), anyInt());
    }
}