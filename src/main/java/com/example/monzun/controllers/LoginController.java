package com.example.monzun.controllers;

import com.example.monzun.dto.AuthUserDTO;
import com.example.monzun.dto.UserDTO;
import com.example.monzun.entities.Role;
import com.example.monzun.entities.User;
import com.example.monzun.enums.RoleEnum;
import com.example.monzun.repositories.UserRepository;
import com.example.monzun.requests.AuthRequest;
import com.example.monzun.security.JwtUtil;
import com.example.monzun.services.MyUserDetailsService;
import org.springframework.http.HttpStatus;
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
    private final JwtUtil jwtTokenUtil;

    public LoginController(
            AuthenticationManager authenticationManager,
            UserRepository userRepository,
            MyUserDetailsService myUserDetailsService,
            JwtUtil jwtTokenUtil
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.myUserDetailsService = myUserDetailsService;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    /**
     * Создание JWT токена
     *
     * @param request credentials пользователя
     * @return JSON
     */
    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@Valid @RequestBody AuthRequest request) {
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
            return new ResponseEntity<User>(HttpStatus.FORBIDDEN);
        }

        final UserDetails userDetails = myUserDetailsService.loadUserByUsername(request.getEmail());
        final String jwt = jwtTokenUtil.generateToken(userDetails);

        return ResponseEntity.ok(new AuthUserDTO(jwt, new UserDTO(user)));
    }
}
