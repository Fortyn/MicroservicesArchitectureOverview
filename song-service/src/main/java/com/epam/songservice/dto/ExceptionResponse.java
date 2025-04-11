package com.epam.songservice.dto;

public record ExceptionResponse(
        int errorCode,
        String errorMessage
) {

}
