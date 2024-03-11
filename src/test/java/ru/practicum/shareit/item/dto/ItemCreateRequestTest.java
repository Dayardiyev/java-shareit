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
class ItemCreateRequestTest {

    private final JacksonTester<ItemCreateRequest> jacksonTester;

    @SneakyThrows
    @Test
    void convertFromJsonToUserCreateRequestTest() {
        ItemCreateRequest itemCreateRequest = new ItemCreateRequest();
        itemCreateRequest.setName("Item name");
        itemCreateRequest.setDescription("Description text");
        itemCreateRequest.setAvailable(false);

        ItemCreateRequest objectFromJson = jacksonTester.readObject("/item/ItemCreateRequest.json");

        assertThat(objectFromJson, samePropertyValuesAs(itemCreateRequest));
    }
}

