package com.juansecu.restart.core.handlers;

import org.hibernate.exception.JDBCConnectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.juansecu.restart.shared.models.dtos.responses.BasicResDto;

@ControllerAdvice
@Order(1)
public class JpaExceptionHandler {
    private static final Logger CONSOLE_LOGGER = LoggerFactory.getLogger(JpaExceptionHandler.class);

    @ExceptionHandler(JDBCConnectionException.class)
    protected ResponseEntity<BasicResDto> handleJdbcConnectionException(
        final JDBCConnectionException jdbcConnectionException
    ) {
        JpaExceptionHandler.CONSOLE_LOGGER.error(
            "JDBC connection exception: {}",
            jdbcConnectionException.getMessage()
        );

        return new ResponseEntity<>(
            new BasicResDto(
                false,
                HttpStatus.INTERNAL_SERVER_ERROR,
                null,
                "Internal server error",
                null
            ),
            HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
