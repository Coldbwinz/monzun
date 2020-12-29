package com.example.monzun.requests;

import com.example.monzun.validation.rules.ExistsAttachment;
import com.example.monzun.validation.rules.ExistsAttachmentFromList;
import com.example.monzun.validation.rules.UniqueStartupName;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StartupRequest {
    @ExistsAttachment
    private Long logoId;
    @ExistsAttachmentFromList
    private Long[] fileIds;
    @NotNull
    @UniqueStartupName
    @ApiModelProperty(required = true)
    private String name;
    private String description;
    private String businessPlan;
    private String tasks;
    private String growthPlan;
    private String useArea;
    private String points;
}
