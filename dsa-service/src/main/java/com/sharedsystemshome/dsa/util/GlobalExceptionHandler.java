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

import java.time.ZoneId;
import java.time.ZonedDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleAllExceptions(Exception ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Validation error: " + ex.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Validation error: " + ex.getMessage());
    }
    @ExceptionHandler(
            AuthenticationException.class)
    public ResponseEntity<?> handleUnauthorisedExceptions(AuthenticationException ex){

        HttpStatus httpStatus = HttpStatus.UNAUTHORIZED;

        HttpRequestException httpRequestException = new HttpRequestException(
                httpStatus,
                ex.getMessage()
        );

        return new ResponseEntity<>(httpRequestException, httpStatus);
    }

    @ExceptionHandler({BadRequestException.class})
    public ResponseEntity<?> handleBadRequestException(BadRequestException ex){

        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

        HttpRequestException httpRequestException = new HttpRequestException(
                httpStatus,
                ex.getMessage()
        );

        return new ResponseEntity<>(httpRequestException, httpStatus);
    }

    @ExceptionHandler(
            EntityNotFoundException.class)
    public ResponseEntity<?> handleNotFoundExceptions(EntityNotFoundException ex){

        HttpStatus httpStatus = HttpStatus.NOT_FOUND;

        HttpRequestException httpRequestException = new HttpRequestException(
                httpStatus,
                ex.getMessage()
        );

        return new ResponseEntity<>(httpRequestException, httpStatus);
    }

    @ExceptionHandler({
            NullOrEmptyValueException.class,
            NullOrEmptyCollectionException.class,
            BusinessValidationException.class
    })
    public ResponseEntity<?> handleBusinessValidationException(BusinessValidationException ex){

        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

        HttpRequestException httpRequestException = new HttpRequestException(
                httpStatus,
                ex.getMessage()
        );

        return new ResponseEntity<>(httpRequestException, httpStatus);
    }

    @ExceptionHandler({SecurityValidationException.class})
    public ResponseEntity<?> handleSecurityValidationException(SecurityValidationException ex){

        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

        HttpRequestException httpRequestException = new HttpRequestException(
                httpStatus,
                ex.getMessage()
        );

        return new ResponseEntity<>(httpRequestException, httpStatus);
    }

    @ExceptionHandler(
            AddOrUpdateTransactionException.class)
    public ResponseEntity<?> handleInternalServerErrorExceptions(AddOrUpdateTransactionException ex){

        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;

        HttpRequestException httpRequestException = new HttpRequestException(
                httpStatus,
                ex.getMessage()
        );

        return new ResponseEntity<>(httpRequestException, httpStatus);
    }

}
