package com.example.monzun.controllers;

import com.example.monzun.dto.AuthUserDTO;
import com.example.monzun.dto.UserDTO;
import com.example.monzun.entities.Role;
import com.example.monzun.entities.User;
import com.example.monzun.enums.RoleEnum;
import com.example.monzun.repositories.UserRepository;
import com.example.monzun.requests.AuthRequest;
import com.example.monzun.requests.MeRequest;
import com.example.monzun.security.JwtUtil;
import com.example.monzun.services.MyUserDetailsService;
import com.example.monzun.services.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Valid
@RestController
@RequestMapping("api/auth")
public class LoginController extends BaseRestController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final MyUserDetailsService myUserDetailsService;
    private final UserService userService;
    private final JwtUtil jwtTokenUtil;

    public LoginController(
            AuthenticationManager authenticationManager,
            UserRepository userRepository,
            MyUserDetailsService myUserDetailsService,
            JwtUtil jwtTokenUtil,
            UserService userService
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.myUserDetailsService = myUserDetailsService;
        this.userService = userService;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    /**
     * Создание JWT токена
     *
     * @param request credentials пользователя
     * @return JSON
     */
    @ApiOperation(
            value = "Авторизация",
            notes = "Пользователь авторизуется по почте и паролю",
            response = AuthUserDTO.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "Успешно", response = AuthUserDTO.class),
            @ApiResponse(code = 422, message = "Неверные данные для входа")
    })
    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createAuthenticationToken(
            @ApiParam(value = "Данные для входа", required = true)
            @Valid @RequestBody AuthRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(this.getErrorMessage("password", "invalid password"));
        }

        /*
         * Проверка на роль авторизованного пользователя. Допускается трекер и участник.
         */
        Optional<User> possibleUser = userRepository.findByEmail(request.getEmail());

        if (!possibleUser.isPresent()) {
            return new ResponseEntity<User>(HttpStatus.NOT_FOUND);
        }

        User user = possibleUser.get();
        List<Role> availableRoles = Arrays.asList(RoleEnum.STARTUP.getRole(), RoleEnum.TRACKER.getRole());
        if (!availableRoles.contains(user.getRole())) {
            if (user.isBlocked()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(this.getErrorMessage("block_reason", user.getBlockReason()));
            }
            return new ResponseEntity<User>(HttpStatus.FORBIDDEN);
        }

        final UserDetails userDetails = myUserDetailsService.loadUserByUsername(request.getEmail());
        final String jwt = jwtTokenUtil.generateToken(userDetails);

        return ResponseEntity.ok(new AuthUserDTO(jwt, new UserDTO(user)));
    }


    /**
     * Регистрация пользователя
     *
     * @param request данные
     * @return JSON
     */
    @ApiOperation(
            value = "Регистрация",
            response = UserDTO.class
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "Успешно", response = UserDTO.class),
            @ApiResponse(code = 422, message = "Неверные данные")
    })
    @PostMapping(value = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> register(
            @ApiParam(value = "Данные для регистрации", required = true)
            @Valid @RequestBody MeRequest request) {
        User user = userService.create(request);
        return ResponseEntity.ok(new UserDTO(user));
    }
}
