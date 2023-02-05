package com.hasitha.employeemanagment.config;

import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class DefaultExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({ AuthenticationException.class })
    @ResponseBody
    public ResponseEntity<?> handleAuthenticationException(Exception ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failure");
    }

    @ExceptionHandler({ ExpiredJwtException.class })
    @ResponseBody
    public ResponseEntity<?> handleExpiredJWTException(Exception ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("JWT token expired");
    }

    @ExceptionHandler({ UsernameNotFoundException.class })
    @ResponseBody
    public ResponseEntity<?> handleUsernameNotFoundException(Exception ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid user credentials");
    }
}
