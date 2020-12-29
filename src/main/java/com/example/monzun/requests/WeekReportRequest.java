package com.example.monzun.requests;

import com.example.monzun.validation.rules.ExistsAttachmentFromList;
import com.example.monzun.validation.rules.ValidWeekReportEstimate;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WeekReportRequest {
    @ExistsAttachmentFromList
    private Long[] fileIds;
    @NotNull(message = "Week is required")
    @Positive(message = "Week should be positive")
    @ApiParam(required = true)
    private Integer week;
    @NotNull(message = "Estimate is required")
    @Positive(message = "Estimate should be greater 0")
    @ValidWeekReportEstimate
    @ApiParam(required = true)
    private Integer estimate;
    private String comment;
}
