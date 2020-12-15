package com.example.monzun.services;

import com.example.monzun.entities.Attachment;
import com.example.monzun.entities.User;
import com.example.monzun.exception.FileIsEmptyException;
import com.example.monzun.exception.UserByEmailNotFoundException;
import com.example.monzun.repositories.AttachmentRepository;
import com.example.monzun.repositories.UserRepository;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final UserRepository userRepository;

    public AttachmentService(AttachmentRepository attachmentRepository, UserRepository userRepository) {
        this.attachmentRepository = attachmentRepository;
        this.userRepository = userRepository;
    }

    private final String UPLOAD_PATH = System.getProperty("user.dir") + "/attachments/";

    /**
     * Хранение файлы и создание записи в БД
     *
     * @param file file
     * @return Attachment файл
     * @throws IOException IOException
     */
    public Attachment storeFile(MultipartFile file) throws IOException {
        Attachment attachment;
        if (file.isEmpty()) {
            throw new FileIsEmptyException("File is empty " + file.getName());
        }

        attachment = this.saveAttachment(file);
        saveFile(file);

        return attachment;
    }

    /**
     * Файл для скачивания по Attachment UUID
     *
     * @param Uuid UUID файла
     * @return Resource
     * @throws FileNotFoundException FileNotFoundException
     */
    public Resource download(UUID Uuid) throws FileNotFoundException {
        try {
            Attachment attachment = attachmentRepository.findByuuid(Uuid);
            Path filePath = Paths.get(attachment.getPath());

            if (!filePath.toFile().exists()) {
                throw new FileNotFoundException("file not found " + Uuid);
            }

            return new UrlResource(filePath.toUri());
        } catch (MalformedURLException ex) {
            throw new FileNotFoundException("File not found " + Uuid);
        }
    }

    /**
     * Удаление файла (физическое и удаление записи из БД)
     *
     * @param uuid UUID Attachment
     * @return boolean
     * @throws IOException IOException
     */
    public boolean delete(UUID uuid) throws IOException {
        Attachment attachment = attachmentRepository.findByuuid(uuid);

        if (attachment == null) {
            return false;
        }

        Files.delete(Paths.get(attachment.getPath()));
        attachmentRepository.delete(attachment);

        return true;
    }

    /**
     * Физическое сохранение файла
     *
     * @param file файл
     * @throws IOException IOException
     */
    private void saveFile(MultipartFile file) throws IOException {
        Path path = Paths.get(UPLOAD_PATH + file.getOriginalFilename());
        Files.write(path, file.getBytes());
    }


    /**
     * Запись Attachment в БД
     *
     * @param file файл
     * @return Attachment
     */
    private Attachment saveAttachment(MultipartFile file) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> owner = userRepository.findByEmail(email);

        if (!owner.isPresent()) {
            throw new UserByEmailNotFoundException("User with email " + email + " not found");
        }

        String baseURL = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        Attachment attachment = new Attachment();

        attachment.setUrl(baseURL + "/api/attachment/download/" + attachment.getUuid());
        attachment.setFilename(file.getName());
        attachment.setOriginalFilename(file.getOriginalFilename());
        attachment.setPath(UPLOAD_PATH + file.getOriginalFilename());
        attachment.setOwner(owner.get());
        attachment.setFilename(file.getName());
        attachment.setCreatedAt(LocalDateTime.now());
        attachmentRepository.save(attachment);

        return attachment;
    }
}
