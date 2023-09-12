package com.terron.exception;

import com.terron.response.InputValidationErrorDetails;
import com.terron.response.ResponseDetails;
import com.terron.response.TerronException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GeneralExceptionHandler  extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid
            (MethodArgumentNotValidException exception, HttpHeaders headers, HttpStatus status, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        exception.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField().toLowerCase();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return new ResponseEntity<>(new InputValidationErrorDetails(LocalDateTime.now(), errors, request.getDescription(false)), HttpStatus.BAD_REQUEST);
    }

    // Handle specific exceptions
    @ExceptionHandler(TerronException.class)
    public ResponseEntity<?> handleUserException (TerronException exception, WebRequest request) {
        ResponseDetails responseDetails = new ResponseDetails(LocalDateTime.now(), exception.getMessage(), "error");
        return new ResponseEntity<>(responseDetails, HttpStatus.BAD_REQUEST);
    }

    // Handle global exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGlobalException (Exception exception, WebRequest request) {
        ResponseDetails responseDetails = new ResponseDetails(LocalDateTime.now(), exception.getMessage(), "error");
        return new ResponseEntity<>(responseDetails, HttpStatus.BAD_REQUEST);
    }
}
