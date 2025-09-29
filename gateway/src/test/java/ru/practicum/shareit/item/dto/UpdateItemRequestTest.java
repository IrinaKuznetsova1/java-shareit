package ru.practicum.shareit.item.dto;

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
class UpdateItemRequestTest {
    private final JacksonTester<UpdateItemRequest> json;
    private final ObjectMapper objectMapper;
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    private UpdateItemRequest updItem;

    @BeforeEach
    void setup() {
        updItem = new UpdateItemRequest();
    }

    @Test
    void testSerialize() throws Exception {
        updItem.setName("name");
        updItem.setDescription("description");
        updItem.setAvailable(true);
        JsonContent<UpdateItemRequest> result = json.write(updItem);

        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(updItem.getName());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(updItem.getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(updItem.getAvailable());
    }

    @Test
    void testDeserialize() throws Exception {
        String updItemString = "{\"name\":\"name\",\"description\":\"description\", \"available\":\"true\"}";
        UpdateItemRequest result = objectMapper.readValue(updItemString, UpdateItemRequest.class);

        AssertionsForClassTypes.assertThat(result.getName()).isEqualTo("name");
        AssertionsForClassTypes.assertThat(result.getDescription()).isEqualTo("description");
        AssertionsForClassTypes.assertThat(result.getAvailable()).isEqualTo(true);
    }

    @Test
    void testValidation() {
        updItem.setName("name");
        updItem.setDescription("description");
        updItem.setAvailable(true);
        Set<ConstraintViolation<UpdateItemRequest>> constraintViolations = validator.validate(updItem);
        Assertions.assertThat(constraintViolations).hasSize(0);

        updItem.setName(new String(new char[257]));
        constraintViolations = validator.validate(updItem);
        Assertions.assertThat(constraintViolations).hasSize(1);

        updItem.setDescription(new String(new char[513]));
        constraintViolations = validator.validate(updItem);
        Assertions.assertThat(constraintViolations).hasSize(2);

        Assertions.assertThat(constraintViolations).extracting(ConstraintViolation::getMessage).containsExactlyInAnyOrder(
                "максимальная длина названия - 256 символов",
                "максимальная длина описания - 512 символов"
        );
    }
}