package com.example.monzun.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Getter
@Setter
@NoArgsConstructor
public class TaskDTO {
    private Long id;
    private TrackingListDTO tracking;
    private StartupListDTO startup;
    private UserListDTO owner;
    private TaskStatusDTO status;
    private String name;
    private String description;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime deadlineAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime updatedAt;
    private List<AttachmentShortDTO> attachments;
}


