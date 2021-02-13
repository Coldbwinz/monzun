package com.example.monzun.services;

import com.example.monzun.dto.AttachmentShortDTO;
import com.example.monzun.dto.WeekReportDTO;
import com.example.monzun.entities.*;
import com.example.monzun.enums.RoleEnum;
import com.example.monzun.exception.WeekReportNotAllowedException;
import com.example.monzun.repositories.*;
import com.example.monzun.requests.WeekReportRequest;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.EntityNotFoundException;
import javax.validation.ValidationException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class WeekReportService {

    private final WeekReportRepository weekReportRepository;
    private final ModelMapper modelMapper;
    private final StartupRepository startupRepository;
    private final TrackingRepository trackingRepository;
    private final StartupTrackingRepository startupTrackingRepository;
    private final AttachmentService attachmentService;
    private final AttachmentRepository attachmentRepository;
    private final TransactionTemplate transactionTemplate;

    public WeekReportService(
            WeekReportRepository weekReportRepository,
            ModelMapper modelMapper,
            StartupRepository startupRepository,
            TrackingRepository trackingRepository,
            StartupTrackingRepository startupTrackingRepository,
            AttachmentService attachmentService,
            AttachmentRepository attachmentRepository,
            PlatformTransactionManager transactionManager) {
        this.weekReportRepository = weekReportRepository;
        this.modelMapper = modelMapper;
        this.startupRepository = startupRepository;
        this.trackingRepository = trackingRepository;
        this.startupTrackingRepository = startupTrackingRepository;
        this.attachmentService = attachmentService;
        this.attachmentRepository = attachmentRepository;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    /**
     * Детальный просмотр отчета трекера.
     *
     * @param weekReportId ID отчета
     * @param user         Трекер, запрашивающий отчет
     * @return WeekReportDTO
     * @throws EntityNotFoundException EntityNotFoundException
     * @throws AccessDeniedException   AccessDeniedException
     */
    public WeekReportDTO show(Long weekReportId, User user) throws EntityNotFoundException, AccessDeniedException {
        WeekReport weekReport = weekReportRepository.findById(weekReportId)
                .orElseThrow(() -> new EntityNotFoundException("Week report not found id " + weekReportId));

        if (user.getRole().equals(RoleEnum.STARTUP.getRole())) {
            if (!weekReport.getStartup().getOwner().equals(user)) {
                throw new AccessDeniedException("This week report id "
                        + weekReportId + " allowed to for this user " + user.toString());
            }
        } else if (user.getRole().equals(RoleEnum.TRACKER.getRole())) {
            if (!weekReport.getOwner().equals(user)) {
                throw new AccessDeniedException("This week report id "
                        + weekReportId + " allowed to for this user " + user.toString());
            }
        } else {
            throw new AccessDeniedException("This week report id "
                    + weekReportId + " allowed to for this user " + user.toString());
        }

        weekReport.setAttachmentsDTO(getWeekReportsAttachmentDTOs(weekReport));

        return convertToDTO(weekReport);
    }


    /**
     * Получение прикрепленных файлов еженедельных отчетов
     *
     * @param weekReport отчет
     * @return List<AttachmentShortDTO>
     */
    public List<AttachmentShortDTO> getWeekReportsAttachmentDTOs(WeekReport weekReport) {
        return weekReportRepository
                .getWeekReportAttachments(weekReport)
                .stream()
                .map(attachmentService::convertToShortDto)
                .collect(Collectors.toList());
    }


    /**
     * @param trackingId        ID  набор
     * @param startupId         ID стартапа
     * @param weekReportRequest параметры отчета
     * @param user              Трекер, создавший отчет
     * @return WeekReportDTO
     * @throws EntityNotFoundException EntityNotFoundException
     */
    public WeekReportDTO create(Long trackingId, Long startupId, WeekReportRequest weekReportRequest, User user)
            throws EntityNotFoundException, ValidationException {
        if (!user.getRole().equals(RoleEnum.TRACKER.getRole())) {
            throw new AccessDeniedException("Allowed for trackers only");
        }

        Tracking tracking = trackingRepository.findById(trackingId)
                .orElseThrow(() -> new EntityNotFoundException("Tracking not found with id " + trackingId));

        Startup startup = startupRepository.findById(startupId)
                .orElseThrow(() -> new EntityNotFoundException("Startup not found with id " + startupId));

        if (!startupTrackingRepository.existsByStartupAndTrackingAndTracker(startup, tracking, user)) {
            throw new WeekReportNotAllowedException(tracking, startup, user);
        }

        if (weekReportRepository.existsByTrackingAndStartupAndWeek(tracking,startup,weekReportRequest.getWeek())) {
            throw new ValidationException("Report for week " + weekReportRequest.getWeek() + "currently created ");
        }

        //Валидация переданной недели
        validateTrackingWeek(weekReportRequest.getWeek(), tracking);

        WeekReport weekReport = new WeekReport();

        weekReport.setTracking(tracking);
        weekReport.setStartup(startup);
        weekReport.setOwner(user);
        weekReport.setWeek(tracking.getCurrentWeek());
        weekReport.setEstimate(weekReportRequest.getEstimate());
        weekReport.setComment(weekReportRequest.getComment());
        weekReport.setCreatedAt(LocalDateTime.now());
        weekReport.setUpdatedAt(LocalDateTime.now());

        transactionSaveWeekReport(weekReportRequest, weekReport);
        weekReport.setAttachmentsDTO(getWeekReportsAttachmentDTOs(weekReport));

        return convertToDTO(weekReport);
    }


    /**
     * Редактирование отчета
     *
     * @param reportId          ID отчета
     * @param weekReportRequest параметры отчета
     * @param user              Пользователь, который редактирует отчет
     * @return WeekReportDTO
     * @throws EntityNotFoundException       EntityNotFoundException
     * @throws WeekReportNotAllowedException WeekReportNotAllowedException
     */
    public WeekReportDTO update(Long reportId, WeekReportRequest weekReportRequest, User user)
            throws EntityNotFoundException, WeekReportNotAllowedException, ValidationException {
        WeekReport weekReport = weekReportRepository.findById(reportId)
                .orElseThrow(() -> new EntityNotFoundException("Week report not found id" + reportId));

        if (!weekReport.getOwner().equals(user)) {
            throw new WeekReportNotAllowedException(weekReport.getTracking(), weekReport.getStartup(), user);
        }

        //Валидация переданной недели
        validateTrackingWeek(weekReportRequest.getWeek(), weekReport.getTracking());

        weekReport.setWeek(weekReportRequest.getWeek());
        weekReport.setEstimate(weekReportRequest.getEstimate());
        weekReport.setComment(weekReportRequest.getComment());
        weekReport.setUpdatedAt(LocalDateTime.now());

        transactionSaveWeekReport(weekReportRequest, weekReport);
        weekReport.setAttachmentsDTO(getWeekReportsAttachmentDTOs(weekReport));

        return convertToDTO(weekReport);
    }


    /**
     * Удаление еженедельного отчета
     *
     * @param reportId ID отчета
     */
    public void delete(Long reportId, User user) throws EntityNotFoundException, WeekReportNotAllowedException {
        WeekReport weekReport = weekReportRepository.findById(reportId)
                .orElseThrow(() -> new EntityNotFoundException("Week report not found id" + reportId));

        if (!weekReport.getOwner().equals(user)) {
            throw new WeekReportNotAllowedException(weekReport.getTracking(), weekReport.getStartup(), user);
        }

        weekReportRepository.delete(weekReport);
    }


    /**
     * Валидация недели для отчета о работе стартапа.
     * Правила валидации:
     * 1.Неделя не должна превышать актуальной недели набора
     *
     * @param week     неделя
     * @param tracking набор
     */
    private void validateTrackingWeek(int week, Tracking tracking) throws ValidationException {
        if (week > tracking.getCurrentWeek()) {
            throw new ValidationException("week greater then current tracking week");
        }
    }


    /**
     * Обработка транзакции сохранения еженедельного отчета и прикрепленных к нему файлов.
     *
     * @param request    WeekReportRequest
     * @param weekReport Еженедельный отчет
     */
    private void transactionSaveWeekReport(WeekReportRequest request, WeekReport weekReport) {
        //если файлы не загружались, остается пустая коллекция
        List<Attachment> attachments = request.getFileIds() != null
                ? attachmentRepository.findAllById(Arrays.asList(request.getFileIds()))
                : Collections.emptyList();

        try {
            transactionTemplate.executeWithoutResult(exec -> {
                if (!attachments.isEmpty()) {
                    attachmentService.saveWeekReportFiles(weekReport, attachments);
                }
                weekReportRepository.save(weekReport);
            });
        } catch (Exception e) {
            if (!attachments.isEmpty()) {
                for (Attachment attachment : attachments) {
                    try {
                        attachmentService.delete(attachment.getUuid());
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
            }
        }
    }


    /**
     * Преобразование отчета в DTO
     *
     * @param weekReport очтет
     * @return WeekReportDTO
     */
    public WeekReportDTO convertToDTO(WeekReport weekReport) {
        return modelMapper.map(weekReport, WeekReportDTO.class);
    }
}
