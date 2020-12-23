package com.example.monzun.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "task_statuses", schema = "public")
public class TaskStatus {

    @Id
    @Column(name = "task_status_id", updatable = false, nullable = false)
    @SequenceGenerator(name = "task_status_seq",
            sequenceName = "task_statuses_task_status_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "task_status_seq")
    private int taskStatusId;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "alias", nullable = false)
    private String alias;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskStatus taskStatus = (TaskStatus) o;
        return taskStatusId == taskStatus.taskStatusId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskStatusId);
    }
}
