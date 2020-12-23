package com.example.monzun.entities;

import com.example.monzun.dto.AttachmentShortDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "week_reports", schema = "public")
public class WeekReport {
    @Id
    @Column(name = "report_id", updatable = false, nullable = false)
    @SequenceGenerator(name = "week_reports_seq",
            sequenceName = "week_reports_report_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "week_reports_seq")
    private Long id;
    @ManyToOne(targetEntity = Tracking.class)
    @JoinColumn(name = "tracking_id", nullable = false)
    private Tracking tracking;
    @ManyToOne(targetEntity = Startup.class)
    @JoinColumn(name = "startup_id", nullable = false)
    private Startup startup;
    @ManyToOne(targetEntity = User.class)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;
    @Column(name = "week", nullable = false)
    private Integer week;
    @Column(name = "estimate")
    private Integer estimate;
    @Column(name = "comment")
    private String comment;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    @Transient
    private List<AttachmentShortDTO> attachmentsDTO;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WeekReport that = (WeekReport) o;
        return id.equals(that.id) &&
                tracking.equals(that.tracking) &&
                startup.equals(that.startup) &&
                owner.equals(that.owner) &&
                week.equals(that.week) &&
                estimate.equals(that.estimate) &&
                Objects.equals(comment, that.comment) &&
                createdAt.equals(that.createdAt) &&
                Objects.equals(updatedAt, that.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, tracking, startup, owner, week, estimate, comment, createdAt, updatedAt);
    }
}
