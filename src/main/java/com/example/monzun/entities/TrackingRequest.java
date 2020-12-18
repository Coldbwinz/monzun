package com.example.monzun.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tracking_requests", schema = "public")
public class TrackingRequest {
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @SequenceGenerator(name = "trackings_requests_seq",
            sequenceName = "tracking_requests_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "trackings_requests_seq")
    private Long id;
    @ManyToOne(targetEntity = Startup.class)
    @JoinColumn(name = "startup_id", nullable = false)
    private Startup startup;
    @ManyToOne(targetEntity = Tracking.class)
    @JoinColumn(name = "tracking_id", nullable = false)
    private Tracking tracking;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
