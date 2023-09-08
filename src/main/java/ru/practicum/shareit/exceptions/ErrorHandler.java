package ru.practicum.shareit.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.persistence.EntityNotFoundException;
import java.util.Map;
import java.util.NoSuchElementException;

@Slf4j
@RestControllerAdvice("ru.practicum.shareit")
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleValidationException(final ConflictException e) {
        log.info("Статус {}: {}",HttpStatus.CONFLICT,e.getMessage());
        return Map.of("Conflict in data", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleValidationException(final org.springframework.dao.DataIntegrityViolationException e) {
        log.info("Статус {}: {}",HttpStatus.CONFLICT,e.getMessage());
        return Map.of("DataIntegrityViolation in data", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationException(final ValidationException e) {
        log.info("Статус {}: {}",HttpStatus.BAD_REQUEST,e.getMessage());
        return Map.of("Validation Error", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationException(final BadRequestException e) {
        log.info("Статус {}: {}",HttpStatus.BAD_REQUEST,e.getMessage());
        return Map.of("Validation Error", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationException(final MethodArgumentNotValidException e) {
        log.info("Статус {}: {}",HttpStatus.BAD_REQUEST,e.getMessage());
        return Map.of("Validation Error", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFoundException(final NotFoundException e) {
        log.info("Статус {}: {}",HttpStatus.NOT_FOUND,e.getMessage());
        return Map.of("Not found error", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFoundException(final NoSuchElementException e) {
        log.info("Статус {}: {}",HttpStatus.NOT_FOUND,e.getMessage());
        return Map.of("Not found error", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFoundException(final EntityNotFoundException e) {
        log.info("Статус {}: {}",HttpStatus.NOT_FOUND,e.getMessage());
        return Map.of("Not found error", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Map<String, String> handleNotFoundException(final ForbiddenException e) {
        log.info("Статус {}: {}",HttpStatus.FORBIDDEN,e.getMessage());
        return Map.of("Forbidden", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleException(final Exception e) {
        log.info("Статус {}: {}",HttpStatus.INTERNAL_SERVER_ERROR,e.getMessage(),e);
        return new ErrorResponse(e.getMessage());
    }
}
