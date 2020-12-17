package com.example.monzun.dto;

import com.example.monzun.entities.Attachment;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
public class AttachmentShortDTO {
    private Long id;
    private UUID uuid;
    private String url;
    private String originalFilename;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime createdAt;

    public AttachmentShortDTO(Attachment attachment) {
        this.id = attachment.getId();
        this.uuid = attachment.getUuid();
        this.url = attachment.getUrl();
        this.originalFilename = attachment.getOriginalFilename();
        this.createdAt = attachment.getCreatedAt();
    }
}
