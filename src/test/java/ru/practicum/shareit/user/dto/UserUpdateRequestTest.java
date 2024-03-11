package ru.practicum.shareit.user.dto;

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
class UserUpdateRequestTest {

    private final JacksonTester<UserUpdateRequest> jacksonTester;

    @SneakyThrows
    @Test
    void convertFromJsonToUserUpdateRequestTest() {
        UserUpdateRequest userUpdateRequest = new UserUpdateRequest();
        userUpdateRequest.setName("Updated user 1 Name");
        userUpdateRequest.setEmail("updated_user_1@mail.com");

        UserUpdateRequest objectFromJson = jacksonTester.readObject("/user/UserUpdateRequest.json");

        assertThat(objectFromJson, samePropertyValuesAs(userUpdateRequest));
    }
}