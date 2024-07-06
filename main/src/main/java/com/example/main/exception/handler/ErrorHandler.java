package com.example.main.exception.handler;

import com.example.main.exception.errors.DataConflictException;
import com.example.main.exception.errors.ForbiddenOperationException;
import com.example.main.exception.errors.InvalidRequestException;
import com.example.main.exception.errors.NotFoundException;
import com.example.main.exception.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.MethodNotAllowedException;
import org.springframework.web.servlet.NoHandlerFoundException;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler(value = {
            HttpMessageNotReadableException.class,
            NoHandlerFoundException.class,
            MethodNotAllowedException.class,
            HttpRequestMethodNotSupportedException.class,
            NumberFormatException.class,
            MethodArgumentNotValidException.class,
            MethodArgumentTypeMismatchException.class,
            InvalidRequestException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleBadRequest(final Exception e) {
        return new ApiError(null, e.getMessage(), "Incorrectly made request", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {
            NotFoundException.class
    })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotfoundException(final Exception exception) {
        return new ApiError(null, exception.getMessage(), "The required object was not found", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {
            DataConflictException.class,
            DataIntegrityViolationException.class
    })
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleDataConflictException(final Exception e) {
        return new ApiError(null, e.getMessage(), "Integrity constraint has been violated", HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = {
            ForbiddenOperationException.class,
    })
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiError handleForbiddenOperation(final Exception e) {
        return new ApiError(null, e.getMessage(), "The operation can't be executed", HttpStatus.FORBIDDEN);
    }
}