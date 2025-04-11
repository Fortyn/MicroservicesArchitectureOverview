package com.epam.resourceservice.dto;

public record ExceptionResponse(
        int errorCode,
        String errorMessage
) {

}
