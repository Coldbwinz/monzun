package com.example.monzun.services;

import com.example.monzun.dto.AttachmentShortDTO;
import com.example.monzun.dto.StartupDTO;
import com.example.monzun.dto.StartupListDTO;
import com.example.monzun.entities.Attachment;
import com.example.monzun.entities.Startup;
import com.example.monzun.entities.User;
import com.example.monzun.enums.RoleEnum;
import com.example.monzun.exception.StartupAccessNotAllowedException;
import com.example.monzun.exception.StartupCreateNotAllowedException;
import com.example.monzun.repositories.AttachmentRepository;
import com.example.monzun.repositories.StartupRepository;
import com.example.monzun.requests.StartupRequest;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class StartupService {

    private final ModelMapper modelMapper;
    private final StartupRepository startupRepository;
    private final AttachmentRepository attachmentRepository;
    private final AttachmentService attachmentService;
    private final TransactionTemplate transactionTemplate;

    public StartupService(
            ModelMapper modelMapper,
            StartupRepository startupRepository,
            AttachmentRepository attachmentRepository,
            AttachmentService attachmentService,
            PlatformTransactionManager transactionManager
    ) {
        this.modelMapper = modelMapper;
        this.startupRepository = startupRepository;
        this.attachmentRepository = attachmentRepository;
        this.attachmentService = attachmentService;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }


    /**
     * Получение списка стартапов с обработкой разрешения на данное действие.
     *
     * @param user Пользователь, совершающий действие
     * @return List
     */
    public List<StartupListDTO> getStartups(User user) {
        if (user.getRole().equals(RoleEnum.STARTUP.getRole())) {
            return startupRepository
                    .getStartupStartups(user)
                    .stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        } else if (user.getRole().equals(RoleEnum.TRACKER.getRole())) {
            return startupRepository
                    .getTrackerStartups(user)
                    .stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }

    /**
     * Просмотр конкретного стартапа.
     * Если пользователь - участник, то он может обращаться только к своему стартапу.
     * Если пользователь - трекер, то он может обращаться только к тем стартапам, к которым он прикреплен
     *
     * @param id   Startup ID
     * @param user Пользователь
     * @return StartupDTO
     * @throws EntityNotFoundException          не найден стартап
     * @throws StartupAccessNotAllowedException просмотр запрещен
     */
    public StartupDTO getStartup(Long id, User user) throws EntityNotFoundException, StartupAccessNotAllowedException {
        Startup startup = startupRepository.findById(id).orElseThrow(EntityNotFoundException::new);

        if (user.getRole().equals(RoleEnum.STARTUP.getRole())) {
            if (!startup.getOwner().equals(user)) {
                throw new StartupAccessNotAllowedException(startup, user);
            }
        } else if (user.getRole().equals(RoleEnum.TRACKER.getRole())) {
            if (!startupRepository.getStartupTrackers(startup).contains(user)) {
                throw new StartupAccessNotAllowedException(startup, user);
            }
        } else {
            throw new StartupAccessNotAllowedException(startup, user);
        }

        startup.setAttachmentsDTO(getStartupAttachmentDTOs(startup));

        return new StartupDTO(startup);
    }

    /**
     * Получение списка прикрепленных файлов стартапа
     *
     * @param startup стартап
     * @return List
     */
    public List<AttachmentShortDTO> getStartupAttachmentDTOs(Startup startup) {
        return startupRepository
                .getStartupAttachments(startup)
                .stream()
                .map(attachmentService::convertToShortDto)
                .collect(Collectors.toList());
    }

    /**
     * Создание стартапа. Допускается только для участников.
     *
     * @param request параметры стартапа
     * @param owner   пользователь, который выполняет действие
     * @return Startup
     */
    public Startup create(StartupRequest request, User owner) {
        if (!owner.getRole().equals(RoleEnum.STARTUP.getRole())) {
            throw new StartupCreateNotAllowedException(owner);
        }

        Startup startup = new Startup();

        if (request.getLogoId() != null && attachmentRepository.findById(request.getLogoId()).isPresent()) {
            startup.setLogo(attachmentRepository.findById(request.getLogoId()).get());
        }

        startup.setName(request.getName());
        startup.setDescription(request.getDescription());
        startup.setGrowthPlan(request.getGrowthPlan());
        startup.setOwner(owner);
        startup.setPoints(request.getPoints());
        startup.setTasks(request.getTasks());
        startup.setBusinessPlan(request.getBusinessPlan());
        startup.setUseArea(request.getUseArea());
        startup.setCreatedAt(LocalDateTime.now());
        startup.setUpdatedAt(LocalDateTime.now());

        transactionSaveStartup(request, startup);
        startup.setAttachmentsDTO(getStartupAttachmentDTOs(startup));
        return startup;
    }

    /**
     * Редактирование стартапа
     *
     * @param id      Startup ID
     * @param request StartupRequest
     * @param user    Пользователь, который выполняет действие
     * @return Startup
     * @throws EntityNotFoundException EntityNotFoundException
     */
    public Startup update(Long id, StartupRequest request, User user) throws EntityNotFoundException {
        Startup startup = startupRepository.findById(id).orElseThrow(EntityNotFoundException::new);

        if (!startup.getOwner().equals(user)) {
            throw new StartupAccessNotAllowedException(startup, user);
        }

        if (request.getLogoId() != null && attachmentRepository.findById(request.getLogoId()).isPresent()) {
            startup.setLogo(attachmentRepository.findById(request.getLogoId()).get());
        }

        startup.setName(request.getDescription());
        startup.setDescription(request.getDescription());
        startup.setGrowthPlan(request.getGrowthPlan());
        startup.setPoints(request.getPoints());
        startup.setTasks(request.getTasks());
        startup.setBusinessPlan(request.getBusinessPlan());
        startup.setUseArea(request.getUseArea());
        startup.setUpdatedAt(LocalDateTime.now());

        transactionSaveStartup(request, startup);
        startup.setAttachmentsDTO(getStartupAttachmentDTOs(startup));

        startupRepository.saveAndFlush(startup);

        return startup;
    }

    /**
     * Обработка транзакции сохранения стартапа и прикрепленных к нему файлов.
     *
     * @param request StartupRequest
     * @param startup Startup
     */
    private void transactionSaveStartup(StartupRequest request, Startup startup) {
        //если файлы не загружались, остается пустая коллекция
        List<Attachment> attachments = request.getFileIds() != null
                ? attachmentRepository.findAllById(Arrays.asList(request.getFileIds()))
                : Collections.emptyList();

        try {
            transactionTemplate.executeWithoutResult(exec -> {
                if (!attachments.isEmpty()) {
                    attachmentService.saveStartupFiles(startup, attachments);
                }
                startupRepository.save(startup);
            });
        } catch (Exception e) {
            if (!attachments.isEmpty()) {
                for (Attachment attachment : attachments) {
                    try {
                        attachmentService.delete(attachment.getUuid());
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * Преобразование в список DTO
     *
     * @param startup Startup стартап
     * @return StartupListDTO
     */
    private StartupListDTO convertToDto(Startup startup) {
        return modelMapper.map(startup, StartupListDTO.class);
    }
}
