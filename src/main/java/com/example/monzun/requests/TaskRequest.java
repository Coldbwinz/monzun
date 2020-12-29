package com.example.monzun.requests;

import com.example.monzun.validation.rules.ExistsAttachmentFromList;
import com.example.monzun.validation.rules.ExistsTaskStatus;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
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
    @Min(1)
    @Max(5)
    @ExistsTaskStatus
    @ApiModelProperty(required = true)
    private Integer statusId;
    @NotNull(message = "name is required")
    @ApiModelProperty(required = true)
    private String name;
    private String description;
    private LocalDateTime deadlineAt;
}
