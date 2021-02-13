package com.example.monzun.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "attachments", schema = "public")
public class Attachment {
    @Id
    @Column(name = "attachment_id", updatable = false, nullable = false)
    @SequenceGenerator(name = "attachments_seq",
            sequenceName = "attachments_attachment_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "attachments_seq")
    private Long id;
    @Column(name = "uuid")
    private UUID uuid = UUID.randomUUID();
    @Column(name = "url")
    private String url;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;
    @Column(name = "filename", nullable = false)
    private String filename;
    @Column(name = "polytable_type")
    private String polytableType;
    @Column(name = "polytable_id")
    private Long polytableId;
    @Column(name = "original_filename")
    private String originalFilename;
    @Column(name = "path", nullable = false)
    private String path;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
