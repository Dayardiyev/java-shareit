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
class UserCreateRequestTest {

    private final JacksonTester<UserCreateRequest> jacksonTester;

    @SneakyThrows
    @Test
    void convertFromJsonToUserCreateRequestTest() {
        UserCreateRequest userCreateRequest = new UserCreateRequest();
        userCreateRequest.setName("User 1 name");
        userCreateRequest.setEmail("users_1@mail.com");

        UserCreateRequest objectFromJson = jacksonTester.readObject("/user/UserCreateRequest.json");

        assertThat(objectFromJson, samePropertyValuesAs(userCreateRequest));
    }
}