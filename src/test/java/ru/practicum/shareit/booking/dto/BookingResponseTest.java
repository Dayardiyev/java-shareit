package ru.practicum.shareit.booking.dto;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.CommentResponse;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.user.dto.UserResponse;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingResponseTest {

    private final JacksonTester<BookingResponse> jacksonTester;

    @SneakyThrows
    @Test
    void convertUserResponseToJson() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss");
        LocalDateTime created = LocalDateTime.parse("2024.01.30 14:59:59", formatter);
        LocalDateTime start = LocalDateTime.parse("2024.10.30 14:59:59", formatter);
        LocalDateTime end = LocalDateTime.parse("2024.12.12 06:01:01", formatter);

        UserResponse booker = new UserResponse();
        booker.setId(1L);
        booker.setName("User 1 name");
        booker.setEmail("users_1@mail.com");

        CommentResponse commentResponse = new CommentResponse();
        commentResponse.setId(1L);
        commentResponse.setCreated(created);
        commentResponse.setAuthorName("Author name");
        commentResponse.setText("Comment text");

        ItemResponse.BookingView bookingView = new ItemResponse.BookingView();
        bookingView.setId(1L);
        bookingView.setBookerId(1L);

        ItemResponse itemResponse = new ItemResponse();
        itemResponse.setId(1L);
        itemResponse.setName("Item name");
        itemResponse.setAvailable(false);
        itemResponse.setRequestId(1L);
        itemResponse.setDescription("Description text");
        itemResponse.setNextBooking(bookingView);
        itemResponse.setLastBooking(null);
        itemResponse.setComments(Set.of(commentResponse));

        BookingResponse bookingResponse = new BookingResponse();
        bookingResponse.setId(1L);
        bookingResponse.setStart(start);
        bookingResponse.setEnd(end);
        bookingResponse.setStatus(Status.APPROVED);
        bookingResponse.setItem(itemResponse);
        bookingResponse.setBooker(booker);

        JsonContent<BookingResponse> jsonContent = jacksonTester.write(bookingResponse);

        assertThat(jsonContent).isEqualToJson("/booking/BookingResponse.json");
    }
}


