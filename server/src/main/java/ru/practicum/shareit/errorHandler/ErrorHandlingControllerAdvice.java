package ru.practicum.shareit.errorHandler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exceptions.DuplicatedDataException;
import ru.practicum.shareit.exceptions.NotAvailableException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.TimeValidationException;

@Slf4j
@RestControllerAdvice
public class ErrorHandlingControllerAdvice {
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Violation> onNotFoundException(NotFoundException e) {
        log.warn("Обработка исключения NotFoundException: {}", e.getMessage());
        return new ResponseEntity<>(new Violation("id", e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DuplicatedDataException.class)
    public ResponseEntity<Violation> onDuplicatedDataException(DuplicatedDataException e) {
        log.warn("Обработка исключения DuplicatedDataException: {}", e.getMessage());
        return new ResponseEntity<>(new Violation(e.getFieldsName(), e.getMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    public ResponseEntity<String> onRuntimeException(RuntimeException e) {
        log.warn("Обработка исключения RuntimeException: {}", e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(NotAvailableException.class)
    public ResponseEntity<Violation> onNotAvailableException(NotAvailableException e) {
        log.warn("Обработка исключения NotAvailableException: {}", e.getMessage());
        return new ResponseEntity<>(new Violation(e.getFieldNameWithError(), e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TimeValidationException.class)
    public ResponseEntity<Violation> onTimeCrossException(TimeValidationException e) {
        log.warn("Обработка исключения TimeCrossException: {}", e.getMessage());
        return new ResponseEntity<>(new Violation(e.getFieldNameWithError(), e.getMessage()), HttpStatus.BAD_REQUEST);
    }
}