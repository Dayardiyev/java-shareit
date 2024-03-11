package ru.practicum.shareit.booking.dto;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingCreateRequestTest {

    private final JacksonTester<BookingCreateRequest> jacksonTester;

    @SneakyThrows
    @Test
    void convertFromJsonToUserCreateRequestTest() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss");
        LocalDateTime start = LocalDateTime.parse("2024.10.30 14:59:59", formatter);
        LocalDateTime end = LocalDateTime.parse("2024.12.12 06:01:01", formatter);

        BookingCreateRequest bookingCreateRequest = new BookingCreateRequest();
        bookingCreateRequest.setItemId(1L);
        bookingCreateRequest.setStart(start);
        bookingCreateRequest.setEnd(end);

        BookingCreateRequest objectFromJson = jacksonTester.readObject("/booking/BookingCreateRequest.json");

        assertThat(objectFromJson, samePropertyValuesAs(bookingCreateRequest));
    }
}