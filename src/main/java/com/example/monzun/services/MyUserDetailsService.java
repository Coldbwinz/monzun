package com.example.monzun.services;

import com.example.monzun.exception.UserByEmailNotFoundException;
import com.example.monzun.repositories.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class MyUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public MyUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UserByEmailNotFoundException {
        Optional<com.example.monzun.entities.User> possibleUser = userRepository.findByEmail(email);

        if (!possibleUser.isPresent()) {
            throw new UserByEmailNotFoundException("User with email " + email + " not found");
        }

        return new User(
                possibleUser.get().getEmail(),
                possibleUser.get().getPassword(),
                new ArrayList<>()
        );
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}