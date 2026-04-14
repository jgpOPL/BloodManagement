package com.example.bloodmanagement.custom;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class NoUserFoundException extends RuntimeException{
    private String errorMsg;
    private Integer errorCode;
    private LocalDateTime dateTime;

    public NoUserFoundException(String errorMsg,Integer errorCode)
    {
        this.errorCode=errorCode;
        this.errorMsg=errorMsg;
        this.dateTime= LocalDateTime.now();
    }
}
