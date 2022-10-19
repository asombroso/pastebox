package com.pastebox.pastebox.exception.advice;

import com.pastebox.pastebox.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.time.ZonedDateTime;

@ControllerAdvice
public class CustomControllerAdvice {

    @ExceptionHandler({CustomAuthenticationException.class, UserAlreadyExistException.class,
            InvalidCodeException.class})
    public ResponseEntity<?> handle(CustomAuthenticationException ex){
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        ApiException exception = new ApiException(ex.getMessage(), status, ZonedDateTime.now());
        return new ResponseEntity<>(exception, status);
    }

    @ExceptionHandler(NotFoundEntityException.class)
    public ResponseEntity<?> handle(NotFoundEntityException ex){
        HttpStatus status = HttpStatus.NOT_FOUND;
        ApiException exception = new ApiException(ex.getMessage(), status, ZonedDateTime.now());
        return new ResponseEntity<>(exception, status);
    }
}
