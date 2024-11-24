package com.juansecu.restart.shared.models.dtos.responses;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class BasicResDto {
    private boolean isSuccessful;
    private Enum<?> errorCode;
    private List<String> errors;
    private String message;
    private Object data;
}
