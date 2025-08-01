package profect.eatcloud.Config;

import java.util.NoSuchElementException;
import java.util.Map;
import java.util.HashMap;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import profect.eatcloud.Domain.Payment.Exception.PaymentException;
import profect.eatcloud.Domain.Payment.Exception.PaymentNotFoundException;
import profect.eatcloud.Domain.Payment.Exception.PaymentValidationException;
import profect.eatcloud.Domain.Store.Exception.MenuNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({ IllegalArgumentException.class, NoSuchElementException.class })
    public ResponseEntity<String> handleBadRequest(Exception e) {
        return ResponseEntity
                .badRequest()
                .body(e.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handleAccessDenied(AccessDeniedException e) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(e.getMessage());
    }

    @ExceptionHandler(PaymentNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handlePaymentNotFound(PaymentNotFoundException e) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "PAYMENT_NOT_FOUND");
        response.put("message", e.getMessage());
        response.put("errorCode", e.getErrorCode());
        
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }

    @ExceptionHandler(PaymentValidationException.class)
    public ResponseEntity<Map<String, Object>> handlePaymentValidation(PaymentValidationException e) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "PAYMENT_VALIDATION_ERROR");
        response.put("message", e.getMessage());
        response.put("errorCode", e.getErrorCode());
        
        return ResponseEntity
                .badRequest()
                .body(response);
    }

    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<Map<String, Object>> handlePaymentError(PaymentException e) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "PAYMENT_ERROR");
        response.put("message", e.getMessage());
        response.put("errorCode", e.getErrorCode());
        
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }

    @ExceptionHandler(MenuNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleMenuNotFound(MenuNotFoundException e) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", "MENU_NOT_FOUND");
        response.put("message", e.getMessage());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleInternalError(Exception e) {
        return ResponseEntity
                .internalServerError()
                .body(e.getMessage());
    }
}
