package ru.practicum.shareit.user.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.AssertionsForClassTypes;
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
class UpdateUserRequestTest {
    private final JacksonTester<UpdateUserRequest> json;
    private final ObjectMapper objectMapper;
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    private UpdateUserRequest updUser;

    @BeforeEach
    void setup() {
        updUser = new UpdateUserRequest();
    }

    @Test
    void testSerialize() throws Exception {
        updUser.setName("name");
        updUser.setEmail("email@email.com");
        JsonContent<UpdateUserRequest> result = json.write(updUser);

        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(updUser.getName());
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo(updUser.getEmail());
    }

    @Test
    void testDeserialize() throws Exception {
        String updUserString = "{\"name\":\"name\",\"email\":\"email@email.com\"}";
        UpdateUserRequest result = objectMapper.readValue(updUserString, UpdateUserRequest.class);

        AssertionsForClassTypes.assertThat(result.getName()).isEqualTo("name");
        AssertionsForClassTypes.assertThat(result.getEmail()).isEqualTo("email@email.com");
    }

    @Test
    void testValidation() {
        updUser.setName("name");
        updUser.setEmail("email@email.com");
        Set<ConstraintViolation<UpdateUserRequest>> constraintViolations = validator.validate(updUser);
        Assertions.assertThat(constraintViolations).hasSize(0);

        final char[] data = new char[257];
        updUser.setName(new String(data));
        constraintViolations = validator.validate(updUser);
        Assertions.assertThat(constraintViolations).hasSize(1);

        updUser.setEmail(new String(data));
        constraintViolations = validator.validate(updUser);
        Assertions.assertThat(constraintViolations).hasSize(3);

        Assertions.assertThat(constraintViolations).extracting(ConstraintViolation::getMessage).containsExactlyInAnyOrder(
                "максимальная длина имени - 256 символов",
                "строка должна соответствовать формату адреса электронной почты",
                "максимальная длина email - 256 символов"
        );
    }

}