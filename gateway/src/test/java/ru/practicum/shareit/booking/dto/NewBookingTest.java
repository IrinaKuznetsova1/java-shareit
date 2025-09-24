package ru.practicum.shareit.booking.dto;

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

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class NewBookingTest {
    private final JacksonTester<NewBooking> json;
    private final ObjectMapper objectMapper;
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    private NewBooking newBooking;

    @BeforeEach
    void setup() {
        newBooking = new NewBooking();
    }

    @Test
    void testSerialize() throws Exception {
        LocalDateTime start = LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.SECONDS);
        LocalDateTime end = LocalDateTime.now().plusDays(2).truncatedTo(ChronoUnit.SECONDS);
        newBooking.setStart(start);
        newBooking.setEnd(end);
        newBooking.setItemId(1L);

        JsonContent<NewBooking> result = json.write(newBooking);

        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(newBooking.getStart().toString());
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(newBooking.getEnd().toString());
        assertThat(result).extractingJsonPathValue("$.itemId").isEqualTo(1);
    }

    @Test
    void testDeserialize() throws Exception {
        String newBookingString = "{\"start\":\"2025-09-21T19:58:30\",\"end\":\"2025-09-22T19:58:30\", \"itemId\":\"1\"}";
        NewBooking result = objectMapper.readValue(newBookingString, NewBooking.class);

        AssertionsForClassTypes.assertThat(result.getStart()).isEqualTo("2025-09-21T19:58:30");
        AssertionsForClassTypes.assertThat(result.getEnd()).isEqualTo("2025-09-22T19:58:30");
        AssertionsForClassTypes.assertThat(result.getItemId()).isEqualTo(1);
    }

    @Test
    void testValidation() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        newBooking.setStart(start);
        newBooking.setEnd(end);
        newBooking.setItemId(1L);
        Set<ConstraintViolation<NewBooking>> constraintViolations = validator.validate(newBooking);
        Assertions.assertThat(constraintViolations).hasSize(0);

        newBooking.setStart(null);
        constraintViolations = validator.validate(newBooking);
        Assertions.assertThat(constraintViolations).hasSize(1);
        Assertions.assertThat(constraintViolations).extracting(ConstraintViolation::getMessage).containsExactlyInAnyOrder(
                "Дата начала бронирования должна быть указана.");

        newBooking.setStart(LocalDateTime.MIN);
        constraintViolations = validator.validate(newBooking);
        Assertions.assertThat(constraintViolations).hasSize(1);
        Assertions.assertThat(constraintViolations).extracting(ConstraintViolation::getMessage).containsExactlyInAnyOrder(
                "Дата начала бронирования не должна быть прошедшей.");

        newBooking.setEnd(null);
        constraintViolations = validator.validate(newBooking);
        Assertions.assertThat(constraintViolations).hasSize(2);
        Assertions.assertThat(constraintViolations).extracting(ConstraintViolation::getMessage).containsExactlyInAnyOrder(
                "Дата окончания бронирования должна быть указана.",
                "Дата начала бронирования не должна быть прошедшей.");

        newBooking.setEnd(LocalDateTime.MIN);
        constraintViolations = validator.validate(newBooking);
        Assertions.assertThat(constraintViolations).hasSize(2);
        Assertions.assertThat(constraintViolations).extracting(ConstraintViolation::getMessage).containsExactlyInAnyOrder(
                "Дата окончания бронирования не должна быть прошедшей.",
                "Дата начала бронирования не должна быть прошедшей.");

        newBooking.setItemId(null);
        constraintViolations = validator.validate(newBooking);
        Assertions.assertThat(constraintViolations).hasSize(3);
        Assertions.assertThat(constraintViolations).extracting(ConstraintViolation::getMessage).containsExactlyInAnyOrder(
                "Дата окончания бронирования не должна быть прошедшей.",
                "Дата начала бронирования не должна быть прошедшей.",
                "itemId должен быть указан.");

        newBooking.setItemId(0L);
        constraintViolations = validator.validate(newBooking);
        Assertions.assertThat(constraintViolations).hasSize(3);

        Assertions.assertThat(constraintViolations).extracting(ConstraintViolation::getMessage).containsExactlyInAnyOrder(
                "Дата окончания бронирования не должна быть прошедшей.",
                "Дата начала бронирования не должна быть прошедшей.",
                "itemId должен быть больше нуля."
        );
    }
}