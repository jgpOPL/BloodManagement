package com.example.bloodmanagement.exception;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class Response {
    private String message;
    private int status;
    private String path;
    private LocalDateTime dateTime;
}
