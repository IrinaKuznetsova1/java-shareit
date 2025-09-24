package ru.practicum.shareit.request.dto;

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
class NewItemRequestTest {
    private final JacksonTester<NewItemRequest> json;
    private final ObjectMapper objectMapper;
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    private NewItemRequest newItemRequest;

    @BeforeEach
    void setup() {
        newItemRequest = new NewItemRequest();
    }

    @Test
    void testSerialize() throws Exception {
        newItemRequest.setDescription("description");
        JsonContent<NewItemRequest> result = json.write(newItemRequest);

        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(newItemRequest.getDescription());
    }

    @Test
    void testDeserialize() throws Exception {
        String newItemRequestString = "{\"description\":\"description\"}";
        NewItemRequest result = objectMapper.readValue(newItemRequestString, NewItemRequest.class);

        AssertionsForClassTypes.assertThat(result.getDescription()).isEqualTo("description");
    }

    @Test
    void testValidation() {
        newItemRequest.setDescription("description");
        Set<ConstraintViolation<NewItemRequest>> constraintViolations = validator.validate(newItemRequest);
        Assertions.assertThat(constraintViolations).hasSize(0);

        newItemRequest.setDescription(new String(new char[513]));
        constraintViolations = validator.validate(newItemRequest);
        Assertions.assertThat(constraintViolations).hasSize(2);

        Assertions.assertThat(constraintViolations).extracting(ConstraintViolation::getMessage).containsExactlyInAnyOrder(
                "описание не должно быть null или быть пустым",
                "максимальная длина описания - 512 символов"
        );
    }
}