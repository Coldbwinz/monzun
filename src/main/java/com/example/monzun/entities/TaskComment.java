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
@Table(name = "task_comments", schema = "public")
public class TaskComment implements Serializable {
    @Id
    @Column(name = "comment_id", updatable = false, nullable = false)
    @SequenceGenerator(name = "task_comments_seq",
            sequenceName = "task_comments_comment_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "task_comments_seq")
    private Long id;
    @ManyToOne(targetEntity = Task.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;
    @ManyToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;
    @Column(name = "text", nullable = false)
    private String text;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    @Transient
    private List<AttachmentShortDTO> attachmentsDTO;
}
