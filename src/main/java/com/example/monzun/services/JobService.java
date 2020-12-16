package com.example.monzun.services;

import com.example.monzun.jobs.EmailJob;
import org.quartz.*;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.UUID;

@Service
public class JobService {
    private final Scheduler scheduler;

    public JobService(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public void schedule(JobDataMap jobDataMap) throws SchedulerException {
        JobDetail jobDetail = buildJobDetail(jobDataMap);
        Trigger trigger = buildJobTrigger(jobDetail, ZonedDateTime.now());
        scheduler.scheduleJob(jobDetail, trigger);
    }

    public JobDetail buildJobDetail(JobDataMap jobDataMap) {
        return JobBuilder.newJob(EmailJob.class)
                .withIdentity(UUID.randomUUID().toString(), (String) jobDataMap.getOrDefault("job-group", "default"))
                .withDescription(jobDataMap.getString("job-description"))
                .usingJobData(jobDataMap)
                .storeDurably()
                .build();
    }

    public Trigger buildJobTrigger(JobDetail jobDetail, ZonedDateTime startAt) {
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity(jobDetail.getKey().getName())
                .withDescription(jobDetail.getDescription())
                .startAt(Date.from(startAt.toInstant()))
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow())
                .build();
    }
}
