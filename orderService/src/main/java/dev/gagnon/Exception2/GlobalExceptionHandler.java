/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.gagnon.Exception2.GlobalExceptionHandler
 *  dev.gagnon.exception.BusinessException
 *  dev.gagnon.exception.OrderBaseException
 *  dev.gagnon.exception.ResourceNotFoundException
 *  org.springframework.http.HttpStatus
 *  org.springframework.http.HttpStatusCode
 *  org.springframework.http.ResponseEntity
 *  org.springframework.web.bind.annotation.ControllerAdvice
 *  org.springframework.web.bind.annotation.ExceptionHandler
 */
package dev.gagnon.Exception2;

import dev.gagnon.exception.BusinessException;
import dev.gagnon.exception.OrderBaseException;
import dev.gagnon.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice(value={"dev.gagnon.Exception2"})
public class GlobalExceptionHandler {
    @ExceptionHandler(value={ResourceNotFoundException.class})
    public ResponseEntity<String> handleResourceNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(value={OrderBaseException.class})
    public ResponseEntity<String> handleResourceNotFound(OrderBaseException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(value={BusinessException.class})
    public ResponseEntity<String> handleResourceNotFound(BusinessException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(value={RuntimeException.class})
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }

    @ExceptionHandler(value={Exception.class})
    public ResponseEntity<String> handleGenericException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
    }
}

