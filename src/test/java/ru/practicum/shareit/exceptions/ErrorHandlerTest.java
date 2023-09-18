package ru.practicum.shareit.exceptions;

import org.junit.jupiter.api.Test;

import org.springframework.dao.DataIntegrityViolationException;


import javax.persistence.EntityNotFoundException;

import java.util.Map;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class ErrorHandlerTest {

    private final ErrorHandler handler = new ErrorHandler();

    @Test
    void handleValidationException() {
        ValidationException e = new ValidationException("exception");
        Map<String, String> response = handler.handleValidationException(e);
        assertNotNull(response);
    }

    @Test
    void testHandleValidationException() {
        DataIntegrityViolationException e = new DataIntegrityViolationException("exception");
        Map<String, String> response = handler.handleValidationException(e);
        assertNotNull(response);
    }

    @Test
    void testHandleBadRequestException1() {
        BadRequestException e = new BadRequestException("exception");
        Map<String, String> response = handler.handleValidationException(e);
        assertNotNull(response);
        assertEquals("exception", response.get("Validation Error"));
    }

    @Test
    void testHandleNotFoundException() {
        NoSuchElementException e = new NoSuchElementException("exception");
        Map<String, String> response = handler.handleNotFoundException(e);
        assertNotNull(response);
    }

    @Test
    void testHandleNotFoundException2() {
        EntityNotFoundException e = new EntityNotFoundException("exception");
        Map<String, String> response = handler.handleNotFoundException(e);
        assertNotNull(response);
    }

    @Test
    void handleNotFoundException() {
        NotFoundException e = new NotFoundException("exception");
        Map<String, String> response = handler.handleNotFoundException(e);
        assertNotNull(response);
    }

    @Test
    void testHandleNotFoundException1() {
        ForbiddenException e = new ForbiddenException("exception");
        Map<String, String> response = handler.handleNotFoundException(e);
        assertNotNull(response);
    }

    @Test
    void handleException() {
        Exception e = new Exception("exception");
        ErrorResponse response = handler.handleException(e);
        assertNotNull(response);
        assertNotNull(response.getError());
    }
}