package com.example.bloodmanagement.global;

import com.example.bloodmanagement.custom.NoUserFoundException;
import com.example.bloodmanagement.exception.Response;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Response> runTimeException(RuntimeException ex, HttpServletRequest req)
    {
        Response res = new Response();
        res.setMessage(ex.getMessage());
        res.setStatus((HttpStatus.INTERNAL_SERVER_ERROR.value()));
        res.setDateTime(LocalDateTime.now());
        res.setPath(req.getRequestURI());
        return new ResponseEntity<Response>(res,HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(NoUserFoundException.class)
    public ResponseEntity<?> noStudentFoundException(NoUserFoundException ex, HttpServletRequest req)
    {
        Response res = new Response();
        res.setMessage(ex.getErrorMsg());
        res.setStatus(ex.getErrorCode());
        res.setDateTime(ex.getDateTime());
        res.setPath(req.getRequestURI());
        return new ResponseEntity<Response>(res, HttpStatusCode.valueOf(ex.getErrorCode()));
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,String>> validationException(MethodArgumentNotValidException ex)
    {
        Map<String,String> errorMap = new HashMap<>();
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        fieldErrors.forEach(e->errorMap.put(e.getField(),e.getDefaultMessage()));
        return new ResponseEntity<>(errorMap,HttpStatus.BAD_REQUEST);
    }
}
