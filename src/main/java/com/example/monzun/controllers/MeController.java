package com.example.monzun.controllers;

import com.example.monzun.dto.UserDTO;
import com.example.monzun.entities.Mail;
import com.example.monzun.entities.User;
import com.example.monzun.exception.NoAuthUserException;
import com.example.monzun.exception.UserByEmailNotFoundException;
import com.example.monzun.repositories.PasswordResetTokenRepository;
import com.example.monzun.repositories.UserRepository;
import com.example.monzun.requests.MeRequest;
import com.example.monzun.requests.PasswordChangeRequest;
import com.example.monzun.services.EmailService;
import com.example.monzun.services.PasswordResetTokenService;
import com.example.monzun.services.UserService;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

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
     * @return JSON
     */
    @GetMapping()
    public ResponseEntity<?> me() {
        try {
            return ResponseEntity.ok(new UserDTO(this.getAuthUser()));
        } catch (NoAuthUserException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    /**
     * Редактирование авторизованного пользователя
     * @param request MeRequest
     * @return JSON
     */
    @PutMapping()
    public ResponseEntity<?> editMe(@Valid MeRequest request) {
        try {
            User updatedUser = userService.update(getAuthUser().getId(), request);

            return ResponseEntity.ok(new UserDTO(updatedUser));
        } catch (NoAuthUserException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Проверка токена из письма восстановления пароля. Если токен совпадает - редирект на страницу смены пароля.
     *
     * @param token    Токен из письма при запросе смены пароля
     * @param response Response
     * @throws IOException IOException
     */
    @GetMapping("/changePassword")
    public void showChangePasswordPage(@RequestParam("token") String token, HttpServletResponse response) throws IOException {
        boolean isValid = passwordResetTokenService.isValidPasswordResetToken(token);
        String redirectUrl = isValid ? "1" : "2"; //TODO: need links

        response.sendRedirect(redirectUrl);
    }

    /**
     * Формирование токена для сброса пароля и отправка почты с инструкцией по смене пароля
     *
     * @param email Почта пользователя
     * @return JSON
     */
    @PostMapping("/resetPassword")
    public ResponseEntity<?> resetPassword(@RequestParam String email) {
        Optional<User> possibleUser = userRepository.findByEmail(email);
        if (!possibleUser.isPresent()) {
            throw new UserByEmailNotFoundException("User with email " + email + " not found");
        }
        String token = UUID.randomUUID().toString();
        passwordResetTokenService.createPasswordResetTokenForUser(possibleUser.get(), token);

        StringBuilder url = new StringBuilder(ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString());
        url.append("api/me/confirmReset?token=");
        url.append(token);

        Map<String, Object> props = new HashMap<>();
        props.put("name", possibleUser.get().getName());
        props.put("button-url", url);
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
    @PostMapping("/savePassword")
    public ResponseEntity<?> savePassword(@Valid PasswordChangeRequest passwordChangeRequest) {
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
