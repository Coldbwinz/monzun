package com.example.monzun.services;

import com.example.monzun.entities.Attachment;
import com.example.monzun.entities.User;
import com.example.monzun.exception.UniqueUserEmailException;
import com.example.monzun.repositories.AttachmentRepository;
import com.example.monzun.repositories.UserRepository;
import com.example.monzun.requests.MeRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.Optional;


@Service
public class UserService {

    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final AttachmentRepository attachmentRepository;

    public UserService(
            UserRepository userRepository,
            BCryptPasswordEncoder passwordEncoder,
            AttachmentRepository attachmentRepository
    ) {
        this.attachmentRepository = attachmentRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    /**
     * Обновление авторизованного пользователя
     *
     * @param id      ID пользователя
     * @param request параметры пользователя
     * @return User
     */
    public User update(Long id, MeRequest request) throws UniqueUserEmailException {
        User user = getUser(id);

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());

        if (request.getAvatarId() != null) {
            Optional<Attachment> possibleLogo = attachmentRepository.findById(request.getAvatarId());
            possibleLogo.ifPresent(user::setLogo);
        }

        if (!user.getEmail().equals(request.getEmail()) && userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new UniqueUserEmailException();
        }

        if (request.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        user.setUpdatedAt(LocalDateTime.now());

        userRepository.saveAndFlush(user);
        return user;
    }

    /**
     * Изменение пароля пользователя
     *
     * @param id       ID пользователя
     * @param password пароль
     */
    public void changePassword(Long id, String password) {
        User user = getUser(id);
        user.setPassword(passwordEncoder.encode(password));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.saveAndFlush(user);
    }

    /**
     * Получение пользователя по ID
     *
     * @param id User id
     * @return User
     */
    private User getUser(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found"));
    }
}
