package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;
import org.springframework.web.bind.MissingRequestHeaderException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertEquals;

public class ErrorHandlerTest {

    private final ErrorHandler underTest = new ErrorHandler();

    @Test
    public void handleMissingRequestHeaderException() {
        MissingRequestHeaderException exception = mock(MissingRequestHeaderException.class);
        when(exception.getMessage()).thenReturn("message");
        ErrorResponse errorResponse = underTest.handleMissingRequestHeaderException(exception);
        assertEquals("wrong message", "message", errorResponse.getError());
    }

    @Test
    public void handleBadRequestException() {
        ErrorResponse errorResponse = underTest.handleBadRequestException(
                new BadRequestException("message")
        );
        assertEquals("wrong message", "message", errorResponse.getError());
    }

    @Test
    public void handleForbiddenException() {
        ErrorResponse errorResponse = underTest.handleForbiddenException(
                new ForbiddenException("message")
        );
        assertEquals("wrong message", "message", errorResponse.getError());
    }

    @Test
    public void handleNotFoundException() {
        ErrorResponse errorResponse = underTest.handleNotFoundException(
                new NotFoundException("message")
        );
        assertEquals("wrong message", "message", errorResponse.getError());
    }

    @Test
    public void handleConflictException() {
        ErrorResponse errorResponse = underTest.handleConflictException(
                new ConflictException("message")
        );
        assertEquals("wrong message", "message", errorResponse.getError());
    }

}
