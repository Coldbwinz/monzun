package com.example.monzun.repositories;

import com.example.monzun.entities.Startup;
import com.example.monzun.entities.Tracking;
import com.example.monzun.entities.WeekReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WeekReportRepository extends JpaRepository<WeekReport, Long>, WeekReportRepositoryWithJOOQ {
    boolean existsByTrackingAndStartupAndWeek(Tracking tracking, Startup startup, Integer week);
}
