package com.juansecu.restart.auth;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.juansecu.restart.auth.enums.ERegisterError;
import com.juansecu.restart.auth.enums.ERole;
import com.juansecu.restart.auth.events.UserRegisterEvent;
import com.juansecu.restart.auth.models.dtos.requests.RegisterReqDto;
import com.juansecu.restart.auth.models.dtos.responses.RegisterResDto;
import com.juansecu.restart.auth.models.entities.RoleEntity;
import com.juansecu.restart.auth.repositories.IRolesRepository;
import com.juansecu.restart.users.models.entities.UserEntity;
import com.juansecu.restart.users.repositories.IUsersRepository;

@RequiredArgsConstructor
@Service
public class AuthenticationApiService {
    private static final Logger CONSOLE_LOGGER = LoggerFactory.getLogger(AuthenticationApiService.class);

    @Value("${authentication.minimum-user-age}")
    private int minimumUserAge;

    private final ApplicationEventPublisher applicationEventPublisher;
    private final PasswordEncoder passwordEncoder;
    private final IRolesRepository rolesRepository;
    private final IUsersRepository usersRepository;

    public ResponseEntity<RegisterResDto> register(final RegisterReqDto registerReqDto) {
        UserEntity newUser;
        Date newUserBirthdate;

        final Calendar todayAtMinimumUserAgeDate = Calendar.getInstance();
        final RegisterResDto response = new RegisterResDto(
            false,
            ERegisterError.USER_ALREADY_EXISTS,
            List.of("Email or username is already in use"),
            null,
            null
        );
        final Optional<UserEntity> userWithEmail = this.usersRepository.findByEmailIgnoreCase(
            registerReqDto.getEmail()
        );
        final Optional<UserEntity> userWithUsername = this.usersRepository.findByUsernameIgnoreCase(
            registerReqDto.getUsername()
        );

        if (userWithEmail.isPresent() || userWithUsername.isPresent())
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);

        try {
            newUserBirthdate = new SimpleDateFormat("yyyy-MM-dd").parse(
                registerReqDto.getBirthdate()
            );
        } catch (final ParseException parseException) {
            AuthenticationApiService.CONSOLE_LOGGER.error(
                "Error while trying to parse {}'s birthdate: {}",
                registerReqDto.getUsername(),
                parseException.getMessage()
            );

            response.setErrorCode(ERegisterError.INVALID_BIRTHDATE);
            response.setErrors(List.of("Invalid birthdate format"));

            return new ResponseEntity<>(
                response,
                HttpStatus.BAD_REQUEST
            );
        }

        todayAtMinimumUserAgeDate.set(
            Calendar.YEAR,
            todayAtMinimumUserAgeDate.get(Calendar.YEAR) - minimumUserAge
        );

        if (newUserBirthdate.after(todayAtMinimumUserAgeDate.getTime())) {
            AuthenticationApiService.CONSOLE_LOGGER.error(
                "User {} is not at least 13 years old",
                registerReqDto.getUsername()
            );

            response.setErrorCode(ERegisterError.USER_TOO_YOUNG);
            response.setErrors(List.of("User is not at least 13 years old"));

            return new ResponseEntity<>(
                response,
                HttpStatus.BAD_REQUEST
            );
        }

        newUser = new UserEntity(
            registerReqDto.getEmail(),
            registerReqDto.getUsername(),
            this.passwordEncoder.encode(registerReqDto.getPassword()),
            newUserBirthdate
        );

        this.usersRepository.save(newUser);

        AuthenticationApiService.CONSOLE_LOGGER.info(
            "User {} registered successfully",
            registerReqDto.getUsername()
        );

        this.applicationEventPublisher.publishEvent(
            new UserRegisterEvent(newUser)
        );

        response.setSuccessful(true);
        response.setMessage("User " + registerReqDto.getUsername() + " registered successfully");

        return new ResponseEntity<>(
            response,
            HttpStatus.CREATED
        );
    }

    public void setUserAsVerified(final UserEntity user) {
        RoleEntity userRole;
        List<RoleEntity> userRoles;

        userRole = this.rolesRepository
            .findByNameIgnoreCase(ERole.USER.getRole())
            .orElseThrow(
                () -> new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "User role not found"
                )
            );
        userRoles = user.getRoles();

        if (userRoles.contains(userRole)) {
            AuthenticationApiService.CONSOLE_LOGGER.info(
                "{}'s user account is already verified...",
                user.getUsername()
            );

            return;
        }

        AuthenticationApiService.CONSOLE_LOGGER.info(
            "Setting {}'s user account as verified...",
            user.getUsername()
        );

        userRoles.add(userRole);

        user.setRoles(userRoles);

        this.usersRepository.save(user);

        AuthenticationApiService.CONSOLE_LOGGER.info(
            "{}'s user account has been verified successfully...",
            user.getUsername()
        );
    }
}
