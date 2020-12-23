package com.example.monzun.services;

import com.example.monzun.dto.TaskStatusDTO;
import com.example.monzun.entities.Task;
import com.example.monzun.entities.TaskStatus;
import com.example.monzun.entities.User;
import com.example.monzun.enums.RoleEnum;
import com.example.monzun.repositories.TaskRepository;
import com.example.monzun.repositories.TaskStatusRepository;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class TaskStatusService {

    private final ModelMapper modelMapper;
    private final TaskStatusRepository taskStatusRepository;
    private final TaskRepository taskRepository;

    public TaskStatusService(
            ModelMapper modelMapper,
            TaskStatusRepository taskStatusRepository,
            TaskRepository taskRepository
    ) {
        this.modelMapper = modelMapper;
        this.taskStatusRepository = taskStatusRepository;
        this.taskRepository = taskRepository;
    }

    /**
     * Список статусом задач
     *
     * @return List TaskStatusDTO
     */
    public List<TaskStatusDTO> list() {
        return taskStatusRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }


    /**
     * Сменить статус задачи
     *
     * @param taskId       ID задачи
     * @param taskStatusId ID статуса задачи
     * @param user         Пользователь, запросивший обновление статуса
     */
    public void setStatus(Long taskId, Integer taskStatusId, User user)
            throws EntityNotFoundException, AccessDeniedException {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found id " + taskId));

        TaskStatus taskStatus = taskStatusRepository.findById(taskStatusId)
                .orElseThrow(() -> new EntityNotFoundException("Task status not found id " + taskStatusId));

        if (user.getRole().equals(RoleEnum.STARTUP.getRole())) {
            if (!task.getStartup().getOwner().equals(user)) {
                throw new AccessDeniedException("Access denied for user id" + user.getId());
            }

            task.setTaskStatus(taskStatus);
            task.setUpdatedAt(LocalDateTime.now());
            taskRepository.save(task);
        } else if (user.getRole().equals(RoleEnum.TRACKER.getRole())) {
            if (!task.getOwner().equals(user)) {
                throw new AccessDeniedException("Access denied for user id" + user.getId());
            }

            task.setTaskStatus(taskStatus);
            task.setUpdatedAt(LocalDateTime.now());
            taskRepository.save(task);
        } else {
            throw new AccessDeniedException("Access denied for user id" + user.getId());
        }
    }


    /**
     * Преобразование в DTO
     *
     * @param taskStatus статус задачи
     * @return TaskDTO
     */
    public TaskStatusDTO convertToDTO(TaskStatus taskStatus) {
        return modelMapper.map(taskStatus, TaskStatusDTO.class);
    }
}
