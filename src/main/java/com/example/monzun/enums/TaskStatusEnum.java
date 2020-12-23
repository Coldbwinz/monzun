package com.example.monzun.enums;

import com.example.monzun.entities.TaskStatus;

/**
 * Статусы задач
 */
public enum TaskStatusEnum {
    TODO(new TaskStatus(1, "todo", "Планируется")),
    DOING(new TaskStatus(2, "doing", "В работе")),
    IN_CHECK(new TaskStatus(3, "in check", "В проверке")),
    DONE(new TaskStatus(4, "done", "Готово"));

    private final TaskStatus taskStatus;

    TaskStatusEnum(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    public TaskStatus getTaskStatus() {
        return this.taskStatus;
    }
}
