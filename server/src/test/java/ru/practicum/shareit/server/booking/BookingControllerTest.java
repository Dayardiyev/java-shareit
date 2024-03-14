package ru.practicum.shareit.server.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.server.booking.controller.BookingController;
import ru.practicum.shareit.server.booking.dto.BookingCreateRequest;
import ru.practicum.shareit.server.booking.dto.BookingMapper;
import ru.practicum.shareit.server.booking.dto.BookingResponse;
import ru.practicum.shareit.server.booking.model.Booking;
import ru.practicum.shareit.server.booking.model.BookingState;
import ru.practicum.shareit.server.booking.model.Status;
import ru.practicum.shareit.server.booking.service.BookingService;
import ru.practicum.shareit.server.common.ObjectGenerator;
import ru.practicum.shareit.server.item.model.Comment;
import ru.practicum.shareit.server.item.model.Item;
import ru.practicum.shareit.server.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingControllerTest {

    private final ObjectGenerator objectGenerator = new ObjectGenerator();

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private BookingController bookingController;

    @Spy
    private BookingMapper mapper = Mappers.getMapper(BookingMapper.class);

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


    @Test
    void create_whenValidBookingCreateRequest_thenBookingResponseReturned() {
        long userId = 1L;
        bookingCreateRequest.setStart(currentDateTime.plusDays(1));
        bookingCreateRequest.setEnd(currentDateTime.plusDays(2));
        bookingCreateRequest.setItemId(1L);

        when(bookingService.create(anyLong(), any()))
                .thenReturn(booking);

        BookingResponse response = bookingController.create(userId, bookingCreateRequest);

        assertThat(response.getItem().getId(), equalTo(bookingCreateRequest.getItemId()));
        verify(bookingService).create(anyLong(), any());
    }


    @Test
    void approve_whenValidRequest_thenOkStatusReturned() {
        long userId = 1L;
        long bookingId = 1L;
        boolean approved = true;
        booking.setStatus(Status.APPROVED);
        when(bookingService.approve(userId, bookingId, approved))
                .thenReturn(booking);

        BookingResponse response = bookingController.update(userId, bookingId, approved);

        assertThat(response.getStatus(), equalTo(booking.getStatus()));
        verify(bookingService).approve(userId, bookingId, approved);
    }

    @Test
    void getById_whenValidRequest_thenBookingResponseReturned() {
        long userId = 1L;
        long bookingId = 1L;
        when(bookingService.findByUserIdAndId(bookingId, userId))
                .thenReturn(booking);

        BookingResponse response = bookingController.findById(userId, bookingId);

        assertThat(response.getId(), equalTo(booking.getId()));
        assertThat(response.getBooker().getName(), equalTo(booking.getBooker().getName()));
        verify(bookingService).findByUserIdAndId(bookingId, userId);
    }


    @Test
    void getByBookerId_whenValidRequest_thenListOfBookingResponseReturned() {
        long userId = 0L;
        BookingState stateFilter = BookingState.APPROVED;
        int from = 0;
        int size = 50;
        when(bookingService.findAllByBookerId(userId, stateFilter, from, size))
                .thenReturn(List.of(booking));

        List<BookingResponse> response = bookingController.findAllByBookerId(userId, stateFilter.toString(), from, size);

        assertThat(response, hasSize(1));
        assertThat(response.get(0).getStatus(), equalTo(booking.getStatus()));
        verify(bookingService).findAllByBookerId(userId, stateFilter, from, size);
    }

    @Test
    void getByBooker_whenWrongState_thenExceptionThrown() {
        long userId = 0L;
        String stateFilter = "UNKNOWN STATE";
        int from = 0;
        int size = 50;

        assertThrows(IllegalArgumentException.class, () -> bookingController.findAllByBookerId(userId, stateFilter, from, size));
    }

    @Test
    void getByOwner_whenValidRequest_thenListOfBookingResponseReturned() {
        long userId = 0L;
        BookingState stateFilter = BookingState.APPROVED;
        int from = 0;
        int size = 50;
        when(bookingService.findAllByOwnerId(userId, stateFilter, from, size))
                .thenReturn(List.of(booking));

        List<BookingResponse> response = bookingController.findAllByOwnerId(userId, stateFilter.toString(), from, size);

        assertThat(response, hasSize(1));
        assertThat(response.get(0).getStatus(), equalTo(booking.getStatus()));
        verify(bookingService).findAllByOwnerId(userId, stateFilter, from, size);
    }

    @Test
    void getByOwner_whenWrongState_thenExceptionThrown() {
        long userId = 0L;
        String stateFilter = "UNKNOWN STATE";
        int from = 0;
        int size = 50;

        assertThrows(IllegalArgumentException.class, () -> bookingController.findAllByOwnerId(userId, stateFilter, from, size));
    }
}
