package com.example.monzun.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "startup_trackings", schema = "public")
public class StartupTracking {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    @SequenceGenerator(name = "startup_trackings_seq",
            sequenceName = "startup_trackings_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "startup_trackings_seq")
    Long id;
    @ManyToOne(targetEntity = Tracking.class, optional = false)
    @JoinColumn(name = "tracking_id", referencedColumnName = "tracking_id")
    private Tracking tracking;
    @ManyToOne(targetEntity = Startup.class, optional = false)
    @JoinColumn(name = "startup_id", referencedColumnName = "startup_id")
    private Startup startup;
    @ManyToOne(targetEntity = User.class)
    @JoinColumn(name = "tracker_id", referencedColumnName = "user_id")
    private User tracker;
}
