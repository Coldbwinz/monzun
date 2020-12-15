package com.example.monzun.dto;

import com.example.monzun.entities.Role;
import com.example.monzun.entities.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class UserDTO {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private boolean isBlocked;
    private String blockReason;
    private AttachmentShortDTO logo;
    private Role role;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    public UserDTO(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.logo = user.getLogo() != null ? new AttachmentShortDTO(user.getLogo()) : null;
        this.email = user.getEmail();
        this.phone = user.getPhone();
        this.isBlocked = user.isBlocked();
        this.blockReason = user.getBlockReason();
        this.role = user.getRole();
        this.createdAt = user.getCreatedAt();
        this.updatedAt = user.getUpdatedAt();
    }
}
