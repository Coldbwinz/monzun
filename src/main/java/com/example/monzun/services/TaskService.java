package com.example.monzun.services;

import com.example.monzun.dto.AttachmentShortDTO;
import com.example.monzun.dto.TaskDTO;
import com.example.monzun.dto.TaskListDTO;
import com.example.monzun.entities.*;
import com.example.monzun.enums.RoleEnum;
import com.example.monzun.repositories.*;
import com.example.monzun.requests.TaskRequest;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class TaskService {

    private final ModelMapper modelMapper;
    private final TrackingRepository trackingRepository;
    private final StartupRepository startupRepository;
    private final StartupTrackingRepository startupTrackingRepository;
    private final TaskRepository taskRepository;
    private final AttachmentService attachmentService;
    private final AttachmentRepository attachmentRepository;
    private final TransactionTemplate transactionTemplate;
    private final TaskStatusRepository taskStatusRepository;

    public TaskService(
            ModelMapper modelMapper,
            TrackingRepository trackingRepository,
            StartupRepository startupRepository,
            StartupTrackingRepository startupTrackingRepository,
            TaskRepository taskRepository,
            AttachmentService attachmentService,
            AttachmentRepository attachmentRepository,
            TransactionTemplate transactionTemplate,
            TaskStatusRepository taskStatusRepository
    ) {
        this.modelMapper = modelMapper;
        this.trackingRepository = trackingRepository;
        this.startupRepository = startupRepository;
        this.startupTrackingRepository = startupTrackingRepository;
        this.taskRepository = taskRepository;
        this.attachmentService = attachmentService;
        this.attachmentRepository = attachmentRepository;
        this.transactionTemplate = transactionTemplate;
        this.taskStatusRepository = taskStatusRepository;
    }

    /**
     * Список задач для стартапа в наборе.
     *
     * @param trackingId ID набора
     * @param startupId  ID стартапа
     * @param user       Пользователь, запросивший список
     * @return List DTO
     * @throws EntityNotFoundException EntityNotFoundException
     * @throws AccessDeniedException   AccessDeniedException
     */
    public List<TaskListDTO> list(Long trackingId, Long startupId, User user)
            throws EntityNotFoundException, AccessDeniedException {
        Tracking tracking = trackingRepository.findById(trackingId)
                .orElseThrow(() -> new EntityNotFoundException("Tracking not found id " + trackingId));

        Startup startup = startupRepository.findById(startupId)
                .orElseThrow(() -> new EntityNotFoundException("Startup not found id " + startupId));

        StartupTracking startupTracking = startupTrackingRepository.findByTrackingAndStartup(tracking, startup)
                .orElseThrow(() -> new EntityNotFoundException("This tracking " + tracking.getName()
                        + " not contain startup name " + startup.getName()));

        if (user.getRole().equals(RoleEnum.STARTUP.getRole())) {
            if (!startupTracking.getStartup().getOwner().equals(user)) {
                throw new AccessDeniedException("Access denied for user id" + user.getId());
            }

            return taskRepository.findByTracking_IdAndStartup_Id(trackingId, startupId)
                    .stream()
                    .map(this::convertToListDTO)
                    .collect(Collectors.toList());
        } else if (user.getRole().equals(RoleEnum.TRACKER.getRole())) {
            if (!startupTracking.getTracker().equals(user)) {
                throw new AccessDeniedException("Access denied for user id " + user.getId());
            }

            return taskRepository.findByTracking_IdAndStartup_Id(trackingId, startupId)
                    .stream()
                    .map(this::convertToListDTO)
                    .collect(Collectors.toList());
        } else {
            throw new AccessDeniedException("Access denied for user id " + user.getId());
        }
    }


    /**
     * Просмотр задачи
     *
     * @param id   ID задачи
     * @param user Пользователь, запросивший просмотр
     * @return TaskDTO
     */
    public TaskDTO show(Long id, User user) throws AccessDeniedException, EntityNotFoundException {
        Task task = taskRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Task not found id " + id));
        task.setAttachmentsDTO(getTaskAttachmentDTOs(task));

        if (user.getRole().equals(RoleEnum.STARTUP.getRole())) {
            if (!task.getStartup().getOwner().equals(user)) {
                throw new AccessDeniedException("Access denied for user id " + user.getId());
            }

            return convertToDTO(task);
        } else if (user.getRole().equals(RoleEnum.TRACKER.getRole())) {
            if (!task.getOwner().equals(user)) {
                throw new AccessDeniedException("Access denied for user id " + user.getId());
            }

            return convertToDTO(task);
        } else {
            throw new AccessDeniedException("Access denied for user id " + user.getId());
        }
    }

    /**
     * Создание задачи
     *
     * @param trackingId  ID
     * @param startupId   ID
     * @param taskRequest Параметры задачи
     * @param user        Создатель задачи
     * @return TaskDTO
     * @throws EntityNotFoundException EntityNotFoundException
     * @throws AccessDeniedException   AccessDeniedException
     */
    public TaskDTO create(Long trackingId, Long startupId, TaskRequest taskRequest, User user)
            throws EntityNotFoundException, AccessDeniedException {
        Tracking tracking = trackingRepository.findById(trackingId)
                .orElseThrow(() -> new EntityNotFoundException("Tracking not found id " + trackingId));

        Startup startup = startupRepository.findById(startupId)
                .orElseThrow(() -> new EntityNotFoundException("Startup not found id " + startupId));

        if (!startupTrackingRepository.existsByStartupAndTrackingAndTracker(startup, tracking, user)) {
            throw new AccessDeniedException("User id " + user.getId() + " cant create tasks to startup id "
                    + startupId + " in tracking id " + trackingId);
        }

        TaskStatus taskStatus = taskStatusRepository.findById(taskRequest.getStatusId())
                .orElseThrow(() -> new EntityNotFoundException("Task status not found id " + taskRequest.getStatusId()));

        Task task = new Task();

        task.setTracking(tracking);
        task.setStartup(startup);
        task.setOwner(user);
        task.setName(taskRequest.getName());
        task.setTaskStatus(taskStatus);
        task.setDescription(taskRequest.getDescription());
        task.setDeadlineAt(taskRequest.getDeadlineAt());
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());

        transactionSaveTask(taskRequest, task);

        return convertToDTO(task);
    }

    /**
     * Редактирование задачи
     *
     * @param id          ID задачи
     * @param taskRequest параметры задачи
     * @param user        Пользователь, который редактирует задачу
     * @return TaskDTO
     */
    public TaskDTO update(Long id, TaskRequest taskRequest, User user) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Task not found id " + id));
        if (!task.getOwner().equals(user)) {
            throw new AccessDeniedException("User id " + user.getId() + " cant update task id " + id);
        }

        TaskStatus taskStatus = taskStatusRepository.findById(taskRequest.getStatusId())
                .orElseThrow(() -> new EntityNotFoundException("Task status not found id " + taskRequest.getStatusId()));

        task.setName(taskRequest.getName());
        task.setTaskStatus(taskStatus);
        task.setDescription(taskRequest.getDescription());
        task.setDeadlineAt(taskRequest.getDeadlineAt());
        task.setUpdatedAt(LocalDateTime.now());

        transactionSaveTask(taskRequest, task);

        return convertToDTO(task);
    }

    /**
     * Удаление задачи
     *
     * @param id   ID задачи
     * @param user Пользователь, запросивший удаление
     */
    public void delete(Long id, User user) throws EntityNotFoundException, AccessDeniedException {
        Task task = taskRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Task not found id " + id));

        if (!task.getOwner().equals(user)) {
            throw new AccessDeniedException("User id " + user.getId() + " cant delete task id " + id);
        }

        taskRepository.delete(task);
    }

    /**
     * Получение списка прикрепленных файлов задачи
     *
     * @param task задача
     * @return List
     */
    public List<AttachmentShortDTO> getTaskAttachmentDTOs(Task task) {
        return taskRepository
                .getTaskAttachments(task)
                .stream()
                .map(attachmentService::convertToShortDto)
                .collect(Collectors.toList());
    }

    /**
     * Обработка транзакции сохранения задачи и прикрепленных к ней файлов.
     *
     * @param request TaskRequest
     * @param task    Задачи
     */
    private void transactionSaveTask(TaskRequest request, Task task) {
        //если файлы не загружались, остается пустая коллекция
        List<Attachment> attachments = request.getFileIds() != null
                ? attachmentRepository.findAllById(Arrays.asList(request.getFileIds()))
                : Collections.emptyList();

        try {
            transactionTemplate.executeWithoutResult(exec -> {
                if (!attachments.isEmpty()) {
                    attachmentService.saveTaskFiles(task, attachments);
                }
                taskRepository.save(task);
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
     * Преобразование в спиоск DTO
     *
     * @param task Задача
     * @return TaskListDTO
     */
    public TaskListDTO convertToListDTO(Task task) {
        return modelMapper.map(task, TaskListDTO.class);
    }

    /**
     * Преобразование в DTO
     *
     * @param task Задача
     * @return TaskDTO
     */
    public TaskDTO convertToDTO(Task task) {
        return modelMapper.map(task, TaskDTO.class);
    }
}
