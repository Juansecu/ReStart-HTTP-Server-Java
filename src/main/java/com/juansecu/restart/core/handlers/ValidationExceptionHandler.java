package com.juansecu.restart.core.handlers;

import java.util.List;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.juansecu.restart.shared.models.dtos.responses.BasicResDto;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ValidationExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<BasicResDto> handleMethodArgumentNotValidException(
        final MethodArgumentNotValidException methodArgumentNotValidException
    ) {
        final List<String> errorMessages = methodArgumentNotValidException
            .getBindingResult()
            .getAllErrors()
            .stream()
            .map(ObjectError::getDefaultMessage)
            .toList();

        return new ResponseEntity<>(
            new BasicResDto(
                false,
                HttpStatus.BAD_REQUEST,
                errorMessages,
                "Validation failed",
                null
            ),
            HttpStatus.BAD_REQUEST
        );
    }
}
