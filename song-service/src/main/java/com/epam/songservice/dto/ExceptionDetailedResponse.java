package com.epam.songservice.dto;

import java.util.Map;

public record ExceptionDetailedResponse(
        int errorCode,
        String errorMessage,
        Map<String, String> details
) {

}
