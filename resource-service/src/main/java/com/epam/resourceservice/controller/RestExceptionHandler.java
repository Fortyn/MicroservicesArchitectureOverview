package com.epam.resourceservice.controller;

import com.epam.resourceservice.dto.ExceptionResponse;
import com.epam.resourceservice.exception.InvalidInputException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(value = ConstraintViolationException.class)
    public ResponseEntity<ExceptionResponse> constraintViolationException(ConstraintViolationException ex) {
        var violation = ex.getConstraintViolations().iterator().next();
        var body = new ExceptionResponse(HttpStatus.BAD_REQUEST.value(), violation.getMessage());
        return ResponseEntity
                .badRequest()
                .body(body);
    }


    @ExceptionHandler(value = HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ExceptionResponse> httpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException ex) {
        var message = "Invalid file format: %s. Only MP3 files are allowed".formatted(ex.getContentType());
        var body = new ExceptionResponse(HttpStatus.BAD_REQUEST.value(), message);
        return ResponseEntity
                .badRequest()
                .body(body);
    }

    @ExceptionHandler(value = MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ExceptionResponse> methodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException ex) {
        var message = Integer.class.equals(ex.getRequiredType()) ?
                "Invalid value '%s' for ID. Must be a positive integer".formatted(ex.getValue())
                : "Input parameter type mismatch";
        var body = new ExceptionResponse(HttpStatus.BAD_REQUEST.value(), message);
        return ResponseEntity
                .badRequest()
                .body(body);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ExceptionResponse> missingServletRequestParameterException(
            MissingServletRequestParameterException ex) {
        var message = "Missing request parameter %s".formatted(ex.getParameterName());
        var body = new ExceptionResponse(HttpStatus.BAD_REQUEST.value(), message);
        return ResponseEntity
                .badRequest()
                .body(body);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ExceptionResponse> entityNotFoundException(EntityNotFoundException ex) {
        var body = new ExceptionResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(body);
    }

    @ExceptionHandler(value = InvalidInputException.class)
    public ResponseEntity<ExceptionResponse> invalidInputException(InvalidInputException ex) {
        var body = new ExceptionResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        return ResponseEntity
                .badRequest()
                .body(body);
    }


    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ExceptionResponse> internalServerException(Exception ex) {
        log.error(ex.getMessage());
        var body = new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An internal server error has occurred");
        return ResponseEntity
                .internalServerError()
                .body(body);
    }
}
