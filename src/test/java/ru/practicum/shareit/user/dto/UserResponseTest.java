package ru.practicum.shareit.user.dto;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserResponseTest {

    private final JacksonTester<UserResponse> jacksonTester;

    @SneakyThrows
    @Test
    void convertUserResponseToJson() {
        UserResponse userResponse = new UserResponse();
        userResponse.setId(1L);
        userResponse.setName("User 1 name");
        userResponse.setEmail("users_1@mail.com");

        JsonContent<UserResponse> jsonContent = jacksonTester.write(userResponse);

        assertThat(jsonContent).isEqualToJson("/user/UserResponse.json");
    }
}