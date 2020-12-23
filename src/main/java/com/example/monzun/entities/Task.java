package com.example.monzun.entities;

import com.example.monzun.dto.AttachmentShortDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tasks", schema = "public")
public class Task implements Serializable {
    @Id
    @Column(name = "task_id", updatable = false, nullable = false)
    @SequenceGenerator(name = "tasks_seq",
            sequenceName = "tasks_task_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "tasks_seq")
    private Long id;
    @ManyToOne(targetEntity = Tracking.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "tracking_id", nullable = false)
    private Tracking tracking;
    @ManyToOne(targetEntity = Startup.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "startup_id", nullable = false)
    private Startup startup;
    @ManyToOne(targetEntity = User.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;
    @ManyToOne(targetEntity = TaskStatus.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "task_status_id", nullable = false)
    private TaskStatus taskStatus;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "description")
    private String description;
    @Column(name = "deadline_at")
    private LocalDateTime deadlineAt;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    @Transient
    private List<AttachmentShortDTO> attachmentsDTO;
}
