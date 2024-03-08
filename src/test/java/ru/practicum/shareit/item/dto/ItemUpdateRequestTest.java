package ru.practicum.shareit.item.dto;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemUpdateRequestTest {

    private final JacksonTester<ItemUpdateRequest> jacksonTester;

    @SneakyThrows
    @Test
    void convertFromJsonToUserCreateRequestTest() {
        ItemUpdateRequest itemUpdateRequest = new ItemUpdateRequest();
        itemUpdateRequest.setName("Item name updated");
        itemUpdateRequest.setDescription("Description text updated");
        itemUpdateRequest.setAvailable(true);

        JsonContent<ItemUpdateRequest> jsonContent = jacksonTester.write(itemUpdateRequest);

        Assertions.assertThat(jsonContent).isEqualToJson("/item/ItemUpdateRequest.json");
    }
}