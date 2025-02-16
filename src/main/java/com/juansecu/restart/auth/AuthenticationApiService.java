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
import org.springframework.data.redis.core.PartialUpdate;
import org.springframework.data.redis.core.RedisKeyValueTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.juansecu.restart.auth.enums.ERegisterError;
import com.juansecu.restart.auth.enums.EResendVerificationEmailError;
import com.juansecu.restart.auth.enums.ERole;
import com.juansecu.restart.auth.events.UserRegisterEvent;
import com.juansecu.restart.auth.models.ResendVerificationEmailTrial;
import com.juansecu.restart.auth.models.dtos.requests.RegisterReqDto;
import com.juansecu.restart.auth.models.dtos.responses.RegisterResDto;
import com.juansecu.restart.auth.models.dtos.responses.ResendVerificationEmailResDto;
import com.juansecu.restart.auth.models.entities.RoleEntity;
import com.juansecu.restart.auth.repositories.IResendVerificationEmailTrialsRepository;
import com.juansecu.restart.auth.repositories.IRolesRepository;
import com.juansecu.restart.users.models.dtos.responses.UserIdResDto;
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
    private final RedisKeyValueTemplate redisTemplate;
    private final IResendVerificationEmailTrialsRepository resendVerificationEmailTrialsRepository;
    private final IRolesRepository rolesRepository;
    private final IUsersRepository usersRepository;

    public ResponseEntity<RegisterResDto> register(final RegisterReqDto registerReqDto) {
        UserEntity newUser;
        Date newUserBirthdate;

        final Calendar todayAtMinimumUserAgeDate = Calendar.getInstance();
        final RegisterResDto response = new RegisterResDto(
            false,
            ERegisterError.USER_ALREADY_EXISTS,
            null,
            "Email or username is already in use",
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

        response.setData(new UserIdResDto(newUser.getUserId()));
        response.setErrorCode(null);
        response.setErrors(null);
        response.setSuccessful(true);
        response.setMessage("User " + registerReqDto.getUsername() + " registered successfully");

        return new ResponseEntity<>(
            response,
            HttpStatus.CREATED
        );
    }

    public ResponseEntity<ResendVerificationEmailResDto> resendVerificationEmail(final int userId) {
        ResponseEntity<ResendVerificationEmailResDto> response = new ResponseEntity<>(
            new ResendVerificationEmailResDto(
                false,
                EResendVerificationEmailError.USER_NOT_FOUND,
                null,
                "User not found",
                null
            ),
            HttpStatus.NOT_FOUND
        );
        Optional<ResendVerificationEmailTrial> resendVerificationEmailTrial;

        final Calendar today = Calendar.getInstance();
        final Optional<UserEntity> user = this.usersRepository.findById(userId);
        final Calendar userSignupDate = Calendar.getInstance();

        if (user.isEmpty()) {
            AuthenticationApiService.CONSOLE_LOGGER.error(
                "User with ID {} not found",
                userId
            );

            return response;
        }

        resendVerificationEmailTrial = this.resendVerificationEmailTrialsRepository.findById(userId);

        if (resendVerificationEmailTrial.isPresent())
            response = this.validateLastResendVerificationEmailTrial(
                resendVerificationEmailTrial.get(),
                today
            );
        else {
            this.resendVerificationEmailTrialsRepository.save(
                new ResendVerificationEmailTrial(
                    userId,
                    1,
                    new Date()
                )
            );

            userSignupDate.setTime(user.get().getRegisteredAt());
            userSignupDate.add(Calendar.MINUTE, 2);

            response = today.after(userSignupDate)
                ? new ResponseEntity<>(
                    new ResendVerificationEmailResDto(
                        true,
                        null,
                        null,
                        AuthenticationConstants.VERIFICATION_EMAIL_SENDING_SUCCESSFUL_MESSAGE,
                        null
                    ),
                    HttpStatus.CREATED
                )
                : new ResponseEntity<>(
                    new ResendVerificationEmailResDto(
                        false,
                        EResendVerificationEmailError.TOO_MANY_ATTEMPTS,
                        null,
                        "You must wait 2 minutes before trying to resend the verification email",
                        null
                    ),
                    HttpStatus.TOO_MANY_REQUESTS
                );
        }

        if (response.getStatusCode().is2xxSuccessful())
            this.applicationEventPublisher.publishEvent(
                new UserRegisterEvent(user.get())
            );

        return response;
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

    private ResponseEntity<ResendVerificationEmailResDto> validateLastResendVerificationEmailTrial(
        final ResendVerificationEmailTrial trial,
        final Calendar today
    ) {
        boolean hasBeen24HoursSinceLastTrial;

        final Calendar lastTrialDate = Calendar.getInstance();
        final PartialUpdate<ResendVerificationEmailTrial> partialUpdate = new PartialUpdate<>(
            trial.getUserId(),
            ResendVerificationEmailTrial.class
        );
        final ResendVerificationEmailResDto response = new ResendVerificationEmailResDto(
            false,
            EResendVerificationEmailError.TOO_MANY_ATTEMPTS,
            null,
            "You have reached the maximum amount of trials to resend the verification email",
            null
        );


        lastTrialDate.setTime(trial.getUpdatedAt());
        lastTrialDate.add(Calendar.DAY_OF_MONTH, 1);

        hasBeen24HoursSinceLastTrial = today.after(lastTrialDate);

        if (hasBeen24HoursSinceLastTrial) {
            partialUpdate.set("trialsAmount", 1);
            partialUpdate.set("updatedAt", new Date());
            partialUpdate.refreshTtl(true);

            this.redisTemplate.update(partialUpdate);

            response.setSuccessful(true);
            response.setErrorCode(null);
            response.setMessage(
                AuthenticationConstants.VERIFICATION_EMAIL_SENDING_SUCCESSFUL_MESSAGE
            );

            return new ResponseEntity<>(
                response,
                HttpStatus.OK
            );
        }

        if (trial.getTrialsAmount() == 1) {
            lastTrialDate.add(Calendar.MINUTE, 10);

            if (!today.after(lastTrialDate)) {
                response.setMessage(
                    "You must wait 10 minutes before trying to resend the verification email"
                );

                return new ResponseEntity<>(
                    response,
                    HttpStatus.TOO_MANY_REQUESTS
                );
            }

            response.setSuccessful(true);
            response.setErrorCode(null);
            response.setMessage(
                AuthenticationConstants.VERIFICATION_EMAIL_SENDING_SUCCESSFUL_MESSAGE
            );

            return new ResponseEntity<>(
                response,
                HttpStatus.CREATED
            );
        } else if (trial.getTrialsAmount() == 2)
            return new ResponseEntity<>(
                response,
                HttpStatus.TOO_MANY_REQUESTS
            );

        return new ResponseEntity<>(
            response,
            HttpStatus.CREATED
        );
    }
}
