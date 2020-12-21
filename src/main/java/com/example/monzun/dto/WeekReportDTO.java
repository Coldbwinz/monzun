package com.example.monzun.dto;

import com.example.monzun.enums.WeekReportEstimatesEnum;
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
public class WeekReportDTO {
    private Long id;
    private TrackingListDTO tracking;
    private StartupListDTO startup;
    private UserListDTO owner;
    private Integer week;
    private EstimateDTO estimate;
    private String comment;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime updatedAt;
    private List<AttachmentShortDTO> attachments;

    public void setEstimate(Integer score) {
        WeekReportEstimatesEnum estimate = WeekReportEstimatesEnum.findByScore(score);
        assert estimate != null;
        this.estimate = new EstimateDTO(estimate.getScore(), estimate.getDescription());
    }
}


