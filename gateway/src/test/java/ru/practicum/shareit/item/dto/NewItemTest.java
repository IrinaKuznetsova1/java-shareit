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
class NewItemTest {
    private final JacksonTester<NewItem> json;
    private final ObjectMapper objectMapper;
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    private NewItem newItem;

    @BeforeEach
    void setup() {
        newItem = new NewItem();
    }

    @Test
    void testSerialize() throws Exception {
        newItem.setName("name");
        newItem.setDescription("description");
        newItem.setAvailable(true);
        newItem.setRequestId(1L);
        JsonContent<NewItem> result = json.write(newItem);

        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(newItem.getName());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(newItem.getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(newItem.getAvailable());
        assertThat(result).extractingJsonPathValue("$.requestId").isEqualTo(1);
    }

    @Test
    void testDeserialize() throws Exception {
        String newItemString = "{\"name\":\"name\",\"description\":\"description\", \"available\":\"true\", \"requestId\":\"1\"}";
        NewItem result = objectMapper.readValue(newItemString, NewItem.class);

        AssertionsForClassTypes.assertThat(result.getName()).isEqualTo("name");
        AssertionsForClassTypes.assertThat(result.getDescription()).isEqualTo("description");
        AssertionsForClassTypes.assertThat(result.getAvailable()).isEqualTo(true);
        AssertionsForClassTypes.assertThat(result.getRequestId()).isEqualTo(1);
    }

    @Test
    void testValidation() {
        newItem.setName("name");
        newItem.setDescription("description");
        newItem.setAvailable(true);
        newItem.setRequestId(1L);
        Set<ConstraintViolation<NewItem>> constraintViolations = validator.validate(newItem);
        Assertions.assertThat(constraintViolations).hasSize(0);

        newItem.setName(new String(new char[257]));
        constraintViolations = validator.validate(newItem);
        Assertions.assertThat(constraintViolations).hasSize(2);

        newItem.setDescription(new String(new char[513]));
        constraintViolations = validator.validate(newItem);
        Assertions.assertThat(constraintViolations).hasSize(4);

        newItem.setAvailable(null);
        constraintViolations = validator.validate(newItem);
        Assertions.assertThat(constraintViolations).hasSize(5);

        newItem.setRequestId(0L);
        constraintViolations = validator.validate(newItem);
        Assertions.assertThat(constraintViolations).hasSize(6);

        Assertions.assertThat(constraintViolations).extracting(ConstraintViolation::getMessage).containsExactlyInAnyOrder(
                "имя не должно быть null или быть пустым",
                "максимальная длина названия - 256 символов",
                "описание не должно быть null или быть пустым",
                "максимальная длина описания - 512 символов",
                "статус не должен быть null",
                "requestId должен быть больше нуля"
        );
    }
}