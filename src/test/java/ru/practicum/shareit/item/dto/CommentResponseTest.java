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

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class CommentResponseTest {

    private final JacksonTester<CommentResponse> jacksonTester;

    @SneakyThrows
    @Test
    void convertFromJsonToUserCreateRequestTest() {
        CommentResponse commentResponse = new CommentResponse();
        commentResponse.setId(2L);
        commentResponse.setAuthorName("User 1 name");
        commentResponse.setCreated(LocalDateTime.of(2024, 1, 25, 16, 7, 34));
        commentResponse.setText("Add comment from user1");

        JsonContent<CommentResponse> jsonContent = jacksonTester.write(commentResponse);

        Assertions.assertThat(jsonContent).isEqualToJson("/item/CommentResponse.json");
    }
}