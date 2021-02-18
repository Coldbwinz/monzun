package com.example.monzun.controllers;

import com.example.monzun.dto.UserDTO;
import com.example.monzun.entities.Mail;
import com.example.monzun.entities.User;
import com.example.monzun.exception.NoAuthUserException;
import com.example.monzun.exception.UniqueUserEmailException;
import com.example.monzun.repositories.PasswordResetTokenRepository;
import com.example.monzun.repositories.UserRepository;
import com.example.monzun.requests.MeRequest;
import com.example.monzun.requests.PasswordChangeRequest;
import com.example.monzun.services.EmailService;
import com.example.monzun.services.PasswordResetTokenService;
import com.example.monzun.services.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.util.*;

@Validated
@RestController
@RequestMapping("/api/me")
public class MeController extends BaseRestController {

    private final Environment environment;
    private final PasswordResetTokenService passwordResetTokenService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final UserService userService;

    public MeController(
            Environment environment,
            PasswordResetTokenService passwordResetTokenService,
            PasswordResetTokenRepository passwordResetTokenRepository,
            UserRepository userRepository,
            EmailService emailService,
            UserService userService
    ) {
        this.environment = environment;
        this.passwordResetTokenService = passwordResetTokenService;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.userService = userService;
    }

    /**
     * Просмотр авторизованного пользователя
     *
     * @return JSON
     */
    @ApiOperation(
            value = "Получение собственного профиля",
            notes = "Запрос информации об авторизованном пользователе. Используется для просмотра собственного профиля"
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "Успешно", response = UserDTO.class),
            @ApiResponse(code = 401, message = "Пользователь не авторизован")
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> me() {
        try {
            return ResponseEntity.ok(new UserDTO(this.getAuthUser()));
        } catch (NoAuthUserException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    /**
     * Редактирование авторизованного пользователя
     *
     * @param request MeRequest
     * @return JSON
     */
    @ApiOperation(value = "Редактирование собственного профиля")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Успешно", response = UserDTO.class),
            @ApiResponse(code = 401, message = "Пользователь не авторизован"),
            @ApiResponse(code = 404, message = "Пользователь не найден")
    })
    @PutMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> editMe(
            @ApiParam
            @Valid @RequestBody MeRequest request) {
        try {
            User updatedUser = userService.update(getAuthUser().getId(), request);

            return ResponseEntity.ok(new UserDTO(updatedUser));
        } catch (NoAuthUserException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (UniqueUserEmailException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(this.getErrorMessage("email", "existing email"));
        }
    }

    /**
     * Формирование токена для сброса пароля и отправка почты с инструкцией по смене пароля
     *
     * @param email Почта пользователя
     * @return JSON
     */
    @ApiOperation(value = "Сброс пароля", notes = "Отправка почты для сбора пароля с подтверждением в виде токена")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Успешно", response = UserDTO.class),
            @ApiResponse(code = 401, message = "Пользователь не авторизован"),
            @ApiResponse(code = 404, message = "Пользователь не найден")
    })
    @PostMapping(value = "/resetPassword", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> resetPassword(
            @ApiParam(required = true)
            @RequestParam String email) {
        Optional<User> possibleUser = userRepository.findByEmail(email);
        if (!possibleUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(this.getErrorMessage("not_found", "User with email " + email + " not found"));

        }
        String token = UUID.randomUUID().toString();
        passwordResetTokenService.createPasswordResetTokenForUser(possibleUser.get(), token);

        StringBuilder url = new StringBuilder(Objects.requireNonNull(environment.getProperty("CLIENT_APP_URL")));
        url.append("/reestablish?token=");
        url.append(token);

        Map<String, Object> props = new HashMap<>();
        props.put("name", possibleUser.get().getName());
        props.put("buttonUrl", url);
        props.put("sign", environment.getProperty("APP_NAME"));

        Mail mail = emailService.createMail(possibleUser.get().getEmail(), "Reset password", props);

        try {
            emailService.sendEmail(mail, "resetPassword");
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok().body(this.getTrueResponse());
    }

    /**
     * Изменение пароля польльзователя
     *
     * @param passwordChangeRequest структура параметров при запросе
     * @return JSON
     */
    @ApiOperation(value = "Редактирование пароля",
            notes = "Изменение пароля с учетом проверки токена")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Успешно"),
            @ApiResponse(code = 401, message = "Пользователь не авторизован"),
            @ApiResponse(code = 404, message = "Пользователь не найден"),
            @ApiResponse(code = 422, message = "Токен для сброса пароля не валидный")
    })
    @PostMapping(value = "/savePassword", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> savePassword(
            @ApiParam @Valid @RequestBody PasswordChangeRequest passwordChangeRequest) {
        boolean result = passwordResetTokenService.isValidPasswordResetToken(passwordChangeRequest.getToken());

        if (!result) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
        }

        Long userId = passwordResetTokenRepository.findByToken(passwordChangeRequest.getToken()).getUser().getId();

        if (userId != null) {
            userService.changePassword(userId, passwordChangeRequest.getNewPassword());
            return ResponseEntity.status(HttpStatus.OK).build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
