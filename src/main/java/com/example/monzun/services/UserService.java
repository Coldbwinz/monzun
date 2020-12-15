package com.example.monzun.services;

import com.example.monzun.dto.UserListDTO;
import com.example.monzun.entities.User;
import com.example.monzun.repositories.UserRepository;
import org.modelmapper.ModelMapper;

import org.springframework.stereotype.Service;


@Service
public class UserService {

    private final ModelMapper modelMapper;

    public UserService(
            UserRepository userRepository,
            ModelMapper modelMapper
    ) {
        this.modelMapper = modelMapper;
    }

    private UserListDTO convertToDto(User user) {
        return modelMapper.map(user, UserListDTO.class);
    }
}
