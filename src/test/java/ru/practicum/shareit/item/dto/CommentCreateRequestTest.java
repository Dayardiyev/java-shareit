package ru.practicum.shareit.item.dto;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class CommentCreateRequestTest {

    private final JacksonTester<CommentCreateRequest> jacksonTester;

    @SneakyThrows
    @Test
    void convertFromJsonToUserCreateRequestTest() {
        CommentCreateRequest commentCreateRequest = new CommentCreateRequest();
        commentCreateRequest.setText("Add comment from user1");

        CommentCreateRequest objectFromJson = jacksonTester.readObject("/item/CommentCreateRequest.json");

        assertThat(objectFromJson, samePropertyValuesAs(commentCreateRequest));
    }
}