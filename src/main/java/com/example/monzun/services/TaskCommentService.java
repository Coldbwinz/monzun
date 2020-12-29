package com.example.monzun.services;

import com.example.monzun.dto.AttachmentShortDTO;
import com.example.monzun.dto.TaskCommentDTO;
import com.example.monzun.entities.Attachment;
import com.example.monzun.entities.Task;
import com.example.monzun.entities.TaskComment;
import com.example.monzun.entities.User;
import com.example.monzun.enums.RoleEnum;
import com.example.monzun.repositories.AttachmentRepository;
import com.example.monzun.repositories.TaskCommentRepository;
import com.example.monzun.repositories.TaskRepository;
import com.example.monzun.requests.TaskCommentRequest;
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
public class TaskCommentService {

    private final ModelMapper modelMapper;
    private final TaskRepository taskRepository;
    private final AttachmentService attachmentService;
    private final AttachmentRepository attachmentRepository;
    private final TransactionTemplate transactionTemplate;
    private final TaskCommentRepository taskCommentRepository;

    public TaskCommentService(
            ModelMapper modelMapper,
            TaskRepository taskRepository,
            AttachmentService attachmentService,
            AttachmentRepository attachmentRepository,
            TransactionTemplate transactionTemplate,
            TaskCommentRepository taskCommentRepository
    ) {
        this.modelMapper = modelMapper;
        this.taskRepository = taskRepository;
        this.attachmentService = attachmentService;
        this.attachmentRepository = attachmentRepository;
        this.transactionTemplate = transactionTemplate;
        this.taskCommentRepository = taskCommentRepository;
    }

    /**
     * Список комментариев к задаче
     *
     * @param taskId ID задачи
     * @param user   Пользователь, запросивший список комментариев
     * @return List DTO
     * @throws EntityNotFoundException EntityNotFoundException
     * @throws AccessDeniedException   AccessDeniedException
     */
    public List<TaskCommentDTO> list(Long taskId, User user)
            throws EntityNotFoundException, AccessDeniedException {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found id " + taskId));

        List<TaskComment> taskComments = taskCommentRepository.findAllByTask(task);
        for (TaskComment comment : taskComments) {
            comment.setAttachmentsDTO(getTaskCommentAttachmentDTOs(comment));
        }

        if (user.getRole().equals(RoleEnum.STARTUP.getRole())) {
            if (!task.getStartup().getOwner().equals(user)) {
                throw new AccessDeniedException("Access denied for user id" + user.getId());
            }

            return taskComments.stream().map(this::convertToDTO).collect(Collectors.toList());
        } else if (user.getRole().equals(RoleEnum.TRACKER.getRole())) {
            if (!task.getOwner().equals(user)) {
                throw new AccessDeniedException("Access denied for user id " + user.getId());
            }

            return taskComments.stream().map(this::convertToDTO).collect(Collectors.toList());
        } else {
            throw new AccessDeniedException("Access denied for user id " + user.getId());
        }
    }

    /**
     * Создание комментария
     *
     * @param taskId             ID
     * @param taskCommentRequest параметры комментария
     * @param user               Пользователь, создающий комментарий
     * @return DTO
     * @throws EntityNotFoundException EntityNotFoundException
     * @throws AccessDeniedException   AccessDeniedException
     */
    public TaskCommentDTO create(Long taskId, TaskCommentRequest taskCommentRequest, User user)
            throws EntityNotFoundException, AccessDeniedException {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found id " + taskId));

        if (user.getRole().equals(RoleEnum.STARTUP.getRole())) {
            if (!task.getStartup().getOwner().equals(user)) {
                throw new AccessDeniedException("Access denied for user id" + user.getId());
            }
        } else if (user.getRole().equals(RoleEnum.TRACKER.getRole())) {
            if (!task.getOwner().equals(user)) {
                throw new AccessDeniedException("Access denied for user id " + user.getId());
            }
        } else {
            throw new AccessDeniedException("Access denied for user id " + user.getId());
        }
        TaskComment taskComment = new TaskComment();

        taskComment.setTask(task);
        taskComment.setOwner(user);
        taskComment.setText(taskCommentRequest.getText());
        taskComment.setCreatedAt(LocalDateTime.now());
        taskComment.setUpdatedAt(LocalDateTime.now());

        transactionSaveTaskComment(taskCommentRequest, taskComment);

        return convertToDTO(taskComment);
    }


    /**
     * Редактирование комментария
     *
     * @param id                 ID комментария
     * @param taskCommentRequest параметры комментария.
     * @param user               Пользователь, редактирующий комментарии
     * @return DTO
     */
    public TaskCommentDTO update(Long id, TaskCommentRequest taskCommentRequest, User user) {
        TaskComment taskComment = taskCommentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task not found id " + id));

        if (!taskComment.getOwner().equals(user)) {
            throw new AccessDeniedException("User id " + user.getId() + " cant delete task id " + id);
        }

        taskComment.setText(taskCommentRequest.getText());
        taskComment.setUpdatedAt(LocalDateTime.now());

        transactionSaveTaskComment(taskCommentRequest, taskComment);

        return convertToDTO(taskComment);
    }

    /**
     * Удаление комментария
     *
     * @param id   ID комментария
     * @param user Пользователь, запросивший удаление
     */
    public void delete(Long id, User user) throws EntityNotFoundException, AccessDeniedException {
        TaskComment taskComment = taskCommentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task not found id " + id));

        if (!taskComment.getOwner().equals(user)) {
            throw new AccessDeniedException("User id " + user.getId() + " cant delete task id " + id);
        }

        taskCommentRepository.delete(taskComment);
    }

    /**
     * Получение списка прикрепленных файлов задачи
     *
     * @param taskComment комментарий
     * @return List
     */
    public List<AttachmentShortDTO> getTaskCommentAttachmentDTOs(TaskComment taskComment) {
        return taskCommentRepository
                .getTaskCommentAttachments(taskComment)
                .stream()
                .map(attachmentService::convertToShortDto)
                .collect(Collectors.toList());
    }

    /**
     * Обработка транзакции сохранения комментария и прикрепленных к нему файлов.
     *
     * @param request     TaskRequest
     * @param taskComment Комментарий
     */
    private void transactionSaveTaskComment(TaskCommentRequest request, TaskComment taskComment) {
        //если файлы не загружались, остается пустая коллекция
        List<Attachment> attachments = request.getFileIds() != null
                ? attachmentRepository.findAllById(Arrays.asList(request.getFileIds()))
                : Collections.emptyList();

        try {
            transactionTemplate.executeWithoutResult(exec -> {
                if (!attachments.isEmpty()) {
                    attachmentService.saveTaskCommentFiles(taskComment, attachments);
                }
                taskCommentRepository.save(taskComment);
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
     * Преобразование в DTO
     *
     * @param taskComment комментарий к задаче
     * @return TaskCommentDTO
     */
    public TaskCommentDTO convertToDTO(TaskComment taskComment) {
        return modelMapper.map(taskComment, TaskCommentDTO.class);
    }
}
