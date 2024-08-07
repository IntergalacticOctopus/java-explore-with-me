package ru.practicum.ewm.exception.handler;

import org.apache.commons.lang3.exception.ExceptionUtils;
import ru.practicum.ewm.exception.errors.DataConflictException;
import ru.practicum.ewm.exception.errors.ForbiddenOperationException;
import ru.practicum.ewm.exception.errors.InvalidRequestException;
import ru.practicum.ewm.exception.errors.NotFoundException;
import ru.practicum.ewm.exception.model.ApiError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.MethodNotAllowedException;
import org.springframework.web.servlet.NoHandlerFoundException;


@RestControllerAdvice
@Slf4j
public class ErrorHandler {
    @ExceptionHandler(value = {HttpMessageNotReadableException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleHttpMessageNotReadableException(final HttpMessageNotReadableException e) {
        String stacktrace = ExceptionUtils.getStackTrace(e);
        log.error(stacktrace);
        return new ApiError(null, e.getMessage(), "Incorrectly made request", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {NoHandlerFoundException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleNoHandlerFoundException(final NoHandlerFoundException e) {
        String stacktrace = ExceptionUtils.getStackTrace(e);
        log.error(stacktrace);
        return new ApiError(null, e.getMessage(), "Incorrectly made request", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {MethodNotAllowedException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMethodNotAllowedException(final MethodNotAllowedException e) {
        String stacktrace = ExceptionUtils.getStackTrace(e);
        log.error(stacktrace);
        return new ApiError(null, e.getMessage(), "Incorrectly made request", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {HttpRequestMethodNotSupportedException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleHttpRequestMethodNotSupportedException(final HttpRequestMethodNotSupportedException e) {
        String stacktrace = ExceptionUtils.getStackTrace(e);
        log.error(stacktrace);
        return new ApiError(null, e.getMessage(), "Incorrectly made request", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {NumberFormatException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleNumberFormatException(final NumberFormatException e) {
        String stacktrace = ExceptionUtils.getStackTrace(e);
        log.error(stacktrace);
        return new ApiError(null, e.getMessage(), "Incorrectly made request", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        String stacktrace = ExceptionUtils.getStackTrace(e);
        log.error(stacktrace);
        return new ApiError(null, e.getMessage(), "Incorrectly made request", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {MethodArgumentTypeMismatchException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMethodArgumentTypeMismatchException(final MethodArgumentTypeMismatchException e) {
        String stacktrace = ExceptionUtils.getStackTrace(e);
        log.error(stacktrace);
        return new ApiError(null, e.getMessage(), "Incorrectly made request", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {InvalidRequestException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleInvalidRequestException(final InvalidRequestException e) {
        String stacktrace = ExceptionUtils.getStackTrace(e);
        log.error(stacktrace);
        return new ApiError(null, e.getMessage(), "Incorrectly made request", HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(value = {NotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFoundException(final NotFoundException e) {
        String stacktrace = ExceptionUtils.getStackTrace(e);
        log.error(stacktrace);
        return new ApiError(null, e.getMessage(), "The required object was not found", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {DataConflictException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleDataConflictException(final DataConflictException e) {
        String stacktrace = ExceptionUtils.getStackTrace(e);
        log.error(stacktrace);
        return new ApiError(null, e.getMessage(), "Integrity constraint has been violated", HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = {DataIntegrityViolationException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleDataIntegrityViolationException(final DataIntegrityViolationException e) {
        String stacktrace = ExceptionUtils.getStackTrace(e);
        log.error(stacktrace);
        return new ApiError(null, e.getMessage(), "Integrity constraint has been violated", HttpStatus.CONFLICT);
    }


    @ExceptionHandler(value = {ForbiddenOperationException.class,})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiError handleForbiddenOperation(final ForbiddenOperationException e) {
        String stacktrace = ExceptionUtils.getStackTrace(e);
        log.error(stacktrace);
        return new ApiError(null, e.getMessage(), "The operation can't be executed", HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(value = {Exception.class,})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleException(final Exception e) {
        String stacktrace = ExceptionUtils.getStackTrace(e);
        log.error(stacktrace);
        return new ApiError(null, e.getMessage(), "Server error", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = {MissingServletRequestParameterException.class,})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMissingServletRequestParameterException(final MissingServletRequestParameterException e) {
        String stacktrace = ExceptionUtils.getStackTrace(e);
        log.error(stacktrace);
        return new ApiError(null, e.getMessage(), "Incorrect request", HttpStatus.BAD_REQUEST);
    }
}