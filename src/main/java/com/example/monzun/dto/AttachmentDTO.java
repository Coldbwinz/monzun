package com.example.monzun.dto;

import com.example.monzun.entities.Attachment;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class AttachmentDTO {
    private Long id;
    private UUID uuid;
    private String filename;
    private String url;
    private String originalFilename;
    private String path;
    private UserListDTO owner;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime createdAt;

    public AttachmentDTO(Attachment attachment) {
        this.id = attachment.getId();
        this.uuid = attachment.getUuid();
        this.url = attachment.getUrl();
        this.filename = attachment.getFilename();
        this.originalFilename = attachment.getOriginalFilename();
        this.path = attachment.getPath();
        this.owner = new UserListDTO(attachment.getOwner());
        this.createdAt = attachment.getCreatedAt();
    }
}
