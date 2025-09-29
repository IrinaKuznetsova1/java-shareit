package ru.practicum.shareit.user.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class NewUserRequestTest {
    private final JacksonTester<NewUserRequest> json;
    private final ObjectMapper objectMapper;
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    private NewUserRequest newUser;

    @BeforeEach
    void setup() {
        newUser = new NewUserRequest();
    }

    @Test
    void testSerialize() throws Exception {
        newUser.setName("name");
        newUser.setEmail("email@email.com");
        JsonContent<NewUserRequest> result = json.write(newUser);

        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(newUser.getName());
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo(newUser.getEmail());
    }

    @Test
    void testDeserialize() throws Exception {
        String newUserString = "{\"name\":\"name\",\"email\":\"email@email.com\"}";
        NewUserRequest result = objectMapper.readValue(newUserString, NewUserRequest.class);

        assertThat(result.getName()).isEqualTo("name");
        assertThat(result.getEmail()).isEqualTo("email@email.com");
    }

    @Test
    void testValidation() {
        newUser.setName("name");
        newUser.setEmail("email@email.com");
        Set<ConstraintViolation<NewUserRequest>> constraintViolations = validator.validate(newUser);
        Assertions.assertThat(constraintViolations).hasSize(0);

        final char[] data = new char[257];
        newUser.setName(new String(data));
        constraintViolations = validator.validate(newUser);
        Assertions.assertThat(constraintViolations).hasSize(2);

        newUser.setEmail(new String(data));
        constraintViolations = validator.validate(newUser);
        Assertions.assertThat(constraintViolations).hasSize(5);

        Assertions.assertThat(constraintViolations).extracting(ConstraintViolation::getMessage).containsExactlyInAnyOrder(
                "имя не должно быть null или быть пустым",
                "максимальная длина имени - 256 символов",
                "e-mail не должен быть null или быть пустым",
                "строка должна соответствовать формату адреса электронной почты",
                "максимальная длина email - 256 символов"
        );
    }
}