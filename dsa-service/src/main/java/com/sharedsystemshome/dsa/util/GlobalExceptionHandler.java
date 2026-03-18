package com.sharedsystemshome.dsa.util;

import com.sharedsystemshome.dsa.controller.BadRequestException;
import com.sharedsystemshome.dsa.controller.HttpRequestException;
import com.sharedsystemshome.dsa.security.util.AuthenticationException;
import com.sharedsystemshome.dsa.security.util.SecurityValidationException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.persistence.EntityNotFoundException;
import java.util.stream.Collectors;
import org.springframework.validation.FieldError;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<HttpRequestException> handleUnauthorisedExceptions(AuthenticationException ex) {
        return buildResponse(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<HttpRequestException> handleBadRequestException(BadRequestException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<HttpRequestException> handleNotFoundExceptions(EntityNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<HttpRequestException> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex) {

        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::formatFieldError)
                .collect(Collectors.joining("; "));

        if (message.isBlank()) {
            message = "Validation error.";
        }

        return buildResponse(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<HttpRequestException> handleConstraintViolationException(
            ConstraintViolationException ex) {

        String message = ex.getConstraintViolations()
                .stream()
                .map(constraintViolation -> constraintViolation.getPropertyPath() + ": " + constraintViolation.getMessage())
                .collect(Collectors.joining("; "));

        if (message.isBlank()) {
            message = "Validation error.";
        }

        return buildResponse(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler({
            NullOrEmptyValueException.class,
            NullOrEmptyCollectionException.class,
            BusinessValidationException.class
    })
    public ResponseEntity<HttpRequestException> handleBusinessValidationException(RuntimeException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(SecurityValidationException.class)
    public ResponseEntity<HttpRequestException> handleSecurityValidationException(SecurityValidationException ex) {
        return buildResponse(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    @ExceptionHandler(AddOrUpdateTransactionException.class)
    public ResponseEntity<HttpRequestException> handleInternalServerErrorExceptions(
            AddOrUpdateTransactionException ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<HttpRequestException> handleAllExceptions(Exception ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected internal server error.");
    }

    private ResponseEntity<HttpRequestException> buildResponse(HttpStatus httpStatus, String message) {
        HttpRequestException httpRequestException = new HttpRequestException(httpStatus, message);
        return new ResponseEntity<>(httpRequestException, httpStatus);
    }

    private String formatFieldError(FieldError fieldError) {
        return fieldError.getField() + ": " + fieldError.getDefaultMessage();
    }
}
