package com.epam.songservice.controller;

import com.epam.songservice.dto.ExceptionDetailedResponse;
import com.epam.songservice.dto.ExceptionResponse;
import com.epam.songservice.exception.InvalidInputException;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionDetailedResponse> methodArgumentNotValidException(MethodArgumentNotValidException ex) {
        ex.printStackTrace();
        log.error(ex.getMessage());
        var details = parseValidationException(ex.getBindingResult());
        var body = new ExceptionDetailedResponse(HttpStatus.BAD_REQUEST.value(), "Validation error", details);
        return ResponseEntity
                .badRequest()
                .body(body);
    }

    @ExceptionHandler(value = ConstraintViolationException.class)
    public ResponseEntity<ExceptionDetailedResponse> constraintViolationException(ConstraintViolationException ex) {
        ex.printStackTrace();
        log.error(ex.getMessage());
        var details = parseConstraintViolations(ex.getConstraintViolations());
        var body = new ExceptionDetailedResponse(HttpStatus.BAD_REQUEST.value(), "Validation error", details);
        return ResponseEntity
                .badRequest()
                .body(body);
    }

    private Map<String, String> parseConstraintViolations(Set<ConstraintViolation<?>> violations) {
        return violations
                .stream()
                .collect(Collectors.toMap(
                        violation -> ((PathImpl) violation.getPropertyPath()).getLeafNode().asString(),
                        ConstraintViolation::getMessage
                ));
    }


    private Map<String, String> parseValidationException(BindingResult bindingResult) {
        return bindingResult
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        FieldError::getDefaultMessage
                ));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ExceptionResponse> missingServletRequestParameterException(MissingServletRequestParameterException ex) {
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

    @ExceptionHandler(value = EntityExistsException.class)
    public ResponseEntity<ExceptionResponse> entityExistsException(EntityExistsException ex) {
        var body = new ExceptionResponse(HttpStatus.CONFLICT.value(), ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(body);
    }


    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ExceptionResponse> internalServerException(Exception ex) {
        ex.printStackTrace();
        log.error(ex.getMessage());
        var body = new ExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An internal server error has occurred");
        return ResponseEntity
                .internalServerError()
                .body(body);
    }
}
