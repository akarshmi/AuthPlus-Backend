package com.auth.AuthPlus.dtos;

import org.springframework.http.HttpStatus;

public record ErrorResponse(
        String message,
        HttpStatus STATUS,
        int STATUS_CODE
) {
}
