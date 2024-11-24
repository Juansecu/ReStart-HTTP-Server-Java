package com.juansecu.restart.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.juansecu.restart.auth.models.dtos.requests.RegisterReqDto;
import com.juansecu.restart.auth.models.dtos.responses.RegisterResDto;

@RequestMapping("/api/auth")
@RequiredArgsConstructor
@RestController
public class AuthApiController {
    private final AuthenticationApiService authenticationApiService;

    @ApiResponse(
        description = "The user account has been created successfully",
        responseCode = "201"
    )
    @ApiResponse(
        description = "Invalid data provided",
        responseCode = "400"
    )
    @ApiResponse(
        description = "The email or username is already in use",
        responseCode = "409"
    )
    @Operation(
        description = "Register a new user.",
        summary = "Register a new user"
    )
    @PostMapping("/register")
    public ResponseEntity<RegisterResDto> register(
        @RequestBody
        @Valid
        final RegisterReqDto registerReqDto
    ) {
        return this.authenticationApiService.register(registerReqDto);
    }
}
