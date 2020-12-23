package com.example.monzun.requests;

import com.example.monzun.validation.rules.ExistsAttachmentFromList;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskCommentRequest {
    @ExistsAttachmentFromList
    private Long[] fileIds;
    @NotNull(message = "text is required")
    private String text;
}
