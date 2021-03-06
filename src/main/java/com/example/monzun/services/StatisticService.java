package com.example.monzun.services;

import com.example.monzun.entities.Startup;
import com.example.monzun.entities.Tracking;
import com.example.monzun.entities.User;
import com.example.monzun.entities.WeekReport;
import com.example.monzun.enums.RoleEnum;
import com.example.monzun.repositories.StartupRepository;
import com.example.monzun.repositories.StartupTrackingRepository;
import com.example.monzun.repositories.TrackingRepository;
import com.example.monzun.repositories.WeekReportRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.*;


@Service
public class StatisticService {

    private final WeekReportRepository weekReportRepository;
    private final TrackingRepository trackingRepository;
    private final StartupRepository startupRepository;
    private final StartupTrackingRepository startupTrackingRepository;

    public StatisticService(
            WeekReportRepository weekReportRepository,
            TrackingRepository trackingRepository,
            StartupRepository startupRepository,
            StartupTrackingRepository startupTrackingRepository
    ) {
        this.weekReportRepository = weekReportRepository;
        this.trackingRepository = trackingRepository;
        this.startupRepository = startupRepository;
        this.startupTrackingRepository = startupTrackingRepository;
    }

    /**
     * Получение статистики по неделям набора. В статистику входят отчеты трекеров.
     *
     * @param trackingId ID набора
     * @param startupId  ID стартапа
     * @param user       Пользователь, запросивший статистику
     * @return Map
     * @throws EntityNotFoundException EntityNotFoundException
     * @throws AccessDeniedException   AccessDeniedException
     */
    public Map<String, Object> get(Long trackingId, Long startupId, User user)
            throws EntityNotFoundException, AccessDeniedException {
        Tracking tracking = trackingRepository.findById(trackingId)
                .orElseThrow(() -> new EntityNotFoundException("Tracking not found id " + trackingId));

        Startup startup = startupRepository.findById(startupId)
                .orElseThrow(() -> new EntityNotFoundException("Startup not found id " + startupId));

        if (user.getRole().equals(RoleEnum.TRACKER.getRole())) {
            if (!startupTrackingRepository.existsByStartupAndTrackingAndTracker(startup, tracking, user)) {
                throw new AccessDeniedException("Not allowed for this user");
            }
        } else if (user.getRole().equals(RoleEnum.STARTUP.getRole())) {
            if (!startup.getOwner().equals(user)) {
                throw new AccessDeniedException("Not allowed for this user");
            }
        } else {
            throw new AccessDeniedException("Not allowed for this user");
        }

        List<WeekReport> weekReportList = weekReportRepository.getWeekReportByTrackingAndStartup(tracking, startup);

        Map<String, Object> statistic = new HashMap<>();
        Float avgEstimate = !weekReportList.isEmpty()
                ? (float) weekReportList.stream().mapToInt(WeekReport::getEstimate).sum() / weekReportList.size()
                : 0;
        ArrayList<Map<String, Number>> weeksStats = new ArrayList<>();

        for (int weekNumber = 1; weekNumber <= tracking.getCurrentWeek(); weekNumber++) {
            Map<String, Number> weekStats = new HashMap<>();
            weekStats.put("weekNumber", weekNumber);

            int finalWeekNumber = weekNumber;
            Optional<WeekReport> report = weekReportList.stream()
                    .filter(weekReport -> weekReport.getWeek().equals(finalWeekNumber))
                    .findFirst();

            if (!report.isPresent()) {
                weekStats.put("reportId", null);
                weekStats.put("estimate", null);
            } else {
                weekStats.put("reportId", report.get().getId());
                weekStats.put("estimate", report.get().getEstimate());
            }

            weeksStats.add(weekStats);
        }

        statistic.put("weeks", weeksStats);
        statistic.put("avgEstimate", avgEstimate);

        return statistic;
    }
}
