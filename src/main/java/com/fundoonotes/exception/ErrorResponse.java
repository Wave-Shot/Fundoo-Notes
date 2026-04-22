package com.fundoonotes.exception;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private String message;
    private int statusCode;
    private LocalDateTime timestamp;
}