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

import java.util.List;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler(value = {HttpMessageNotReadableException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleHttpMessageNotReadableException(final HttpMessageNotReadableException e) {
        return new ApiError(null, e.getMessage(), "Incorrectly made request", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {NoHandlerFoundException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleNoHandlerFoundException(final NoHandlerFoundException e) {
        return new ApiError(null, e.getMessage(), "Incorrectly made request", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {MethodNotAllowedException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMethodNotAllowedException(final MethodNotAllowedException e) {
        return new ApiError(null, e.getMessage(), "Incorrectly made request", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {HttpRequestMethodNotSupportedException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleHttpRequestMethodNotSupportedException(final HttpRequestMethodNotSupportedException e) {
        return new ApiError(null, e.getMessage(), "Incorrectly made request", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {NumberFormatException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleNumberFormatException(final NumberFormatException e) {
        return new ApiError(null, e.getMessage(), "Incorrectly made request", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        return new ApiError(null, e.getMessage(), "Incorrectly made request", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {MethodArgumentTypeMismatchException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMethodArgumentTypeMismatchException(final MethodArgumentTypeMismatchException e) {
        return new ApiError(null, e.getMessage(), "Incorrectly made request", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {InvalidRequestException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleInvalidRequestException(final InvalidRequestException e) {
        return new ApiError(null, e.getMessage(), "Incorrectly made request", HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(value = {NotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFoundException(final Exception exception) {
        return new ApiError(null, exception.getMessage(), "The required object was not found", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {DataConflictException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleDataConflictException(final DataConflictException e) {
        return new ApiError(null, e.getMessage(), "Integrity constraint has been violated", HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = {DataIntegrityViolationException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleDataIntegrityViolationException(final DataIntegrityViolationException e) {
        return new ApiError(null, e.getMessage(), "Integrity constraint has been violated", HttpStatus.CONFLICT);
    }


    @ExceptionHandler(value = {ForbiddenOperationException.class,})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiError handleForbiddenOperation(final Exception e) {
        return new ApiError(null, e.getMessage(), "The operation can't be executed", HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(value = {Exception.class,})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleException(final Exception e) {
        return new ApiError((List<String>) e, e.getMessage(), "Server error", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}