package com.felippe.banking.shared.api;

import com.felippe.banking.account.application.AccountNotFoundException;
import com.felippe.banking.account.application.DuplicateAccountNumberException;
import com.felippe.banking.customer.application.CustomerNotFoundException;
import com.felippe.banking.customer.application.DuplicateCpfException;
import com.felippe.banking.shared.domain.DomainException;
import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({CustomerNotFoundException.class, AccountNotFoundException.class})
    public ResponseEntity<ErrorResponse> notFound(RuntimeException exception) {
        return error(HttpStatus.NOT_FOUND, "NOT_FOUND", exception.getMessage());
    }

    @ExceptionHandler({DuplicateCpfException.class, DuplicateAccountNumberException.class})
    public ResponseEntity<ErrorResponse> conflict(RuntimeException exception) {
        return error(HttpStatus.CONFLICT, "CONFLICT", exception.getMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class, IllegalArgumentException.class})
    public ResponseEntity<ErrorResponse> invalid(Exception exception) {
        // Não devolve mensagens de parsing ou valores pessoais presentes na requisição.
        return error(HttpStatus.BAD_REQUEST, "INVALID_REQUEST", "Request contains invalid or missing data");
    }

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ErrorResponse> domain(DomainException exception) {
        return error(HttpStatus.UNPROCESSABLE_CONTENT, "DOMAIN_ERROR", exception.getMessage());
    }

    private ResponseEntity<ErrorResponse> error(HttpStatus status, String code, String message) {
        return ResponseEntity.status(status).body(new ErrorResponse(code, message, Instant.now()));
    }
}
