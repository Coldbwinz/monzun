package com.example.monzun.requests;

import com.example.monzun.validation.rules.ExistsAttachmentFromList;
import com.example.monzun.validation.rules.ExistsTaskStatus;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskRequest {
    @ExistsAttachmentFromList
    private Long[] fileIds;
    @NotNull(message = "status is required")
    @ExistsTaskStatus
    private Integer statusId;
    @NotNull(message = "name is required")
    private String name;
    private String description;
    private LocalDateTime deadlineAt;
}
