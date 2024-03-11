package ru.practicum.shareit.item.dto;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;
import java.util.Set;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemResponseTest {

    private final JacksonTester<ItemResponse> jacksonTester;

    @SneakyThrows
    @Test
    void convertFromJsonToUserCreateRequestTest() {
        ItemResponse.BookingView bookingView = new ItemResponse.BookingView();
        bookingView.setId(1L);
        bookingView.setBookerId(2L);

        CommentResponse commentResponse = new CommentResponse();
        commentResponse.setId(1L);
        commentResponse.setCreated(
                LocalDateTime.of(2024, 1, 25, 16, 7, 34));
        commentResponse.setText("Add comment from user1");
        commentResponse.setAuthorName("User 1 name");

        ItemResponse itemResponse = new ItemResponse();
        itemResponse.setId(1L);
        itemResponse.setName("Item name");
        itemResponse.setDescription("Description text");
        itemResponse.setAvailable(false);
        itemResponse.setComments(Set.of(commentResponse));
        itemResponse.setLastBooking(bookingView);

        JsonContent<ItemResponse> jsonContent = jacksonTester.write(itemResponse);

        Assertions.assertThat(jsonContent).isEqualToJson("/item/ItemResponse.json");
    }
}