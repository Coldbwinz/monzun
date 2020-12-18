package com.example.monzun.dto;

import com.example.monzun.entities.Startup;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class StartupDTO {
    private Long id;
    private AttachmentShortDTO logo;
    private UserListDTO owner;
    private String name;
    private String description;
    private String businessPlan;
    private String tasks;
    private String growthPlan;
    private String useArea;
    private String points;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime updatedAt;
    private List<AttachmentShortDTO> attachments;

    public StartupDTO(Startup startup) {
        this.id = startup.getId();
        this.name = startup.getName();
        this.owner = new UserListDTO(startup.getOwner());
        this.logo = startup.getLogo() != null ? new AttachmentShortDTO(startup.getLogo()) : null;
        this.description = startup.getDescription();
        this.businessPlan = startup.getBusinessPlan();
        this.tasks = startup.getTasks();
        this.growthPlan = startup.getGrowthPlan();
        this.points = startup.getPoints();
        this.useArea = startup.getUseArea();
        this.createdAt = startup.getCreatedAt();
        this.updatedAt = startup.getUpdatedAt();
        this.attachments = startup.getAttachmentsDTO();
    }
}
