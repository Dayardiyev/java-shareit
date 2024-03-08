package ru.practicum.shareit.request.dto;

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
class ItemRequestCreateRequestTest {

    private final JacksonTester<ItemRequestCreateRequest> jacksonTester;

    @SneakyThrows
    @Test
    void convertFromJsonToUserCreateRequestTest() {
        ItemRequestCreateRequest itemRequestCreateRequest = new ItemRequestCreateRequest();
        itemRequestCreateRequest.setDescription("Ищу детские коньки 35 размера");

        ItemRequestCreateRequest objectFromJson = jacksonTester.readObject("/request/ItemRequestCreateRequest.json");

        assertThat(objectFromJson, samePropertyValuesAs(itemRequestCreateRequest));
    }
}