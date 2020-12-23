package com.example.monzun.repositories;

import com.example.monzun.entities.Attachment;
import com.example.monzun.entities.WeekReport;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WeekReportRepositoryWithJOOQ {
    List<Attachment> getWeekReportAttachments(WeekReport weekReport);
}