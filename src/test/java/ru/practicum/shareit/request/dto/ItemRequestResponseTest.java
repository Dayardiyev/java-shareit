package ru.practicum.shareit.request.dto;

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
class ItemRequestResponseTest {

    private final JacksonTester<ItemRequestResponse> jacksonTester;

    @SneakyThrows
    @Test
    void convertFromJsonToUserCreateRequestTest() {
        ItemRequestResponse itemRequestResponse = new ItemRequestResponse();
        itemRequestResponse.setId(1L);
        itemRequestResponse.setCreated(
                LocalDateTime.of(2024, 1, 25, 16, 14, 55));
        itemRequestResponse.setDescription("Ищу детские коньки 35 размера");

        JsonContent<ItemRequestResponse> jsonContent = jacksonTester.write(itemRequestResponse);

        Assertions.assertThat(jsonContent).isEqualToJson("/request/ItemRequestResponse.json");
    }
}