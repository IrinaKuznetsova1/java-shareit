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
class NewCommentRequestTest {
    private final JacksonTester<NewCommentRequest> json;
    private final ObjectMapper objectMapper;
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    private NewCommentRequest newComment;

    @BeforeEach
    void setup() {
        newComment = new NewCommentRequest();
    }

    @Test
    void testSerialize() throws Exception {
        newComment.setText("text");
        JsonContent<NewCommentRequest> result = json.write(newComment);

        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo(newComment.getText());
    }

    @Test
    void testDeserialize() throws Exception {
        String newCommentString = "{\"text\":\"text\"}";
        NewCommentRequest result = objectMapper.readValue(newCommentString, NewCommentRequest.class);

        AssertionsForClassTypes.assertThat(result.getText()).isEqualTo("text");
    }

    @Test
    void testValidation() {
        newComment.setText("text");
        Set<ConstraintViolation<NewCommentRequest>> constraintViolations = validator.validate(newComment);
        Assertions.assertThat(constraintViolations).hasSize(0);

        newComment.setText(new String(new char[513]));
        constraintViolations = validator.validate(newComment);
        Assertions.assertThat(constraintViolations).hasSize(2);

        Assertions.assertThat(constraintViolations).extracting(ConstraintViolation::getMessage).containsExactlyInAnyOrder(
                "текст комментария не должен быть null или быть пустым",
                "максимальная длина комментария - 512 символов"
        );
    }
}